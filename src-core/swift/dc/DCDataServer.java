/*****************************************************************************
 * Copyright 2011-2012 INRIA
 * Copyright 2011-2012 Universidade Nova de Lisboa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package swift.dc;

import static sys.net.api.Networking.Networking;
import static sys.utils.Threading.lock;
import static sys.utils.Threading.unlock;

import java.lang.management.LockInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import swift.clocks.CausalityClock;
import swift.clocks.ClockFactory;
import swift.clocks.Timestamp;
import swift.crdt.IntegerCRDT;
import swift.crdt.core.CRDT;
import swift.crdt.core.CRDTIdentifier;
import swift.crdt.core.CRDTObjectUpdatesGroup;
import swift.crdt.core.CRDTOperationDependencyPolicy;
import swift.crdt.core.ManagedCRDT;
import swift.dc.db.DCNodeDatabase;
import swift.dc.db.StatsNodeDatabaseWrapper;
import swift.proto.DHTExecCRDT;
import swift.proto.DHTExecCRDTReply;
import swift.proto.DHTGetCRDT;
import swift.proto.DHTGetCRDTReply;
import swift.proto.ObjectUpdatesInfo;
import swift.proto.SwiftProtocolHandler;
import swift.pubsub.DataServerPubSubService;
import swift.pubsub.SurrogatePubSubService;
import swift.pubsub.UpdateNotification;
import swift.utils.FutureResultHandler;
import sys.dht.DHT_Node;
import sys.net.api.Endpoint;
import sys.net.api.rpc.RpcEndpoint;
import sys.net.api.rpc.RpcHandle;
import sys.net.api.rpc.RpcMessage;
import sys.scheduler.PeriodicTask;
import sys.utils.Threading;

/**
 * Class to maintain data in the server.
 * 
 * @author preguica, smduarte
 */
final class DCDataServer {
    private static Logger logger = Logger.getLogger(DCDataServer.class.getName());

    Map<CRDTIdentifier, LockInfo> locks;
    Map<String, Map<String, CRDTData<?>>> db;

    CausalityClock version;
    CausalityClock cltClock;
    String localSurrogateId;

    DCNodeDatabase dbServer;
    final int pruningInterval;

    Set<CRDTData<?>> modified;

    SurrogatePubSubService suPubSub;
    DataServerPubSubService dsPubSub;

    ExecutorService executor = Executors.newCachedThreadPool();

    RpcEndpoint dhtEndpoint;
    DCSurrogate surrogate;

    CRDTIdentifier heartBeat = new CRDTIdentifier("heart", "beat");

    DCDataServer(final DCSurrogate surrogate, Properties props, SurrogatePubSubService suPubSub, int dhtPort) {
        sys.dht.DHT_Node.DHT_PORT = dhtPort;
        this.surrogate = surrogate;
        this.localSurrogateId = surrogate.getId();
        this.dsPubSub = new DataServerPubSubService(localSurrogateId, executor, surrogate);
        this.suPubSub = suPubSub;

        final String pruningIntervalString = props.getProperty(DCConstants.PRUNING_INTERVAL_PROPERTY);
        if (pruningIntervalString != null) {
            pruningInterval = Integer.valueOf(pruningIntervalString);
        } else {
            pruningInterval = DCConstants.DEFAULT_PRUNING_INTERVAL_MS;
        }
        final String notificationPeriodString = props.getProperty(DCConstants.NOTIFICATION_PERIOD_PROPERTY);
        final int notificationPeriodMillis;
        if (notificationPeriodString != null) {
            notificationPeriodMillis = Integer.valueOf(notificationPeriodString);
        } else {
            notificationPeriodMillis = DCConstants.DEFAULT_NOTIFICATION_PERIOD_MS;
        }

        initStore();
        initData(props);
        initDHT();

        if (logger.isLoggable(Level.INFO)) {
            logger.info("Data server ready...");
        }

        dsPubSub.subscribe(heartBeat, suPubSub);

        new PeriodicTask(1.0, notificationPeriodMillis * 0.001) {
            public void run() {
                dsPubSub.publish(new UpdateNotification(surrogate.surrogateId, new ObjectUpdatesInfo(heartBeat),
                        surrogate.getEstimatedDCVersionCopy()));
            }
        };
    }

    /**
     * Start background thread that dumps to disk
     */
    void initStore() {
        Thread t = new Thread() {
            public void run() {
                for (;;) {
                    try {
                        List<CRDTData<?>> list = null;
                        synchronized (modified) {
                            list = new ArrayList<CRDTData<?>>(modified);
                        }
                        Iterator<CRDTData<?>> it = list.iterator();
                        while (it.hasNext()) {
                            CRDTData<?> obj = it.next();
                            lock(obj.id);
                            try {
                                synchronized (modified) {
                                    modified.remove(obj);
                                }
                            } finally {
                                unlock(obj.id);
                            }
                            writeCRDTintoDB(obj);
                        }
                        logger.info("Flushed store to disk...");
                        Thread.sleep(DCConstants.SYNC_PERIOD);
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }

        };
        t.setDaemon(true);
        t.start();

    }

    /**
     * Start DHT subsystem...
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    void dhtRequest(Endpoint dst, final RpcMessage req, final FutureResultHandler rh) {
        dhtEndpoint.send(dst, req, new SwiftProtocolHandler() {
            public void onReceive(DHTExecCRDTReply reply) {
                rh.onResult(reply.getResult());
            }

            public void onReceive(DHTGetCRDTReply reply) {
                rh.onResult(reply.getObject());
            }
        }, 0);
    }

    @SuppressWarnings("unchecked")
    <V> V dhtRequest(Endpoint dst, final RpcMessage req) {
        final AtomicReference<Object> result = new AtomicReference<Object>(req);
        for (; result.get() == req;) {
            synchronized (result) {
                dhtEndpoint.send(dst, req, new SwiftProtocolHandler() {
                    public void onReceive(DHTExecCRDTReply reply) {
                        result.set(reply.getResult());
                        Threading.synchronizedNotifyAllOn(result);
                    }

                    public void onReceive(DHTGetCRDTReply reply) {
                        result.set(reply.getObject());
                        Threading.synchronizedNotifyAllOn(result);
                    }
                }, 0);
                Threading.waitOn(result, 100);
            }
        }
        return (V) result.get();
    }

    void initDHT() {

        dhtEndpoint = Networking.rpcConnect().toDefaultService();
        Networking.rpcBind(DHT_Node.DHT_PORT).toService(0, new SwiftProtocolHandler() {

            public void onReceive(RpcHandle con, DHTGetCRDT request) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("DHT data server: get CRDT : " + request.getId());
                }
                con.reply(new DHTGetCRDTReply(localGetCRDTObject(con.remoteEndpoint(), request)));
            }

            public void onReceive(RpcHandle con, DHTExecCRDT request) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("DHT data server: exec CRDT : " + request.getGrp().getTargetUID());
                }
                con.reply(new DHTExecCRDTReply(localExecCRDT(request.getGrp(), request.getSnapshotVersion(),
                        request.getTrxVersion(), request.getTxTs(), request.getCltTs(), request.getPrvCltTs(),
                        request.getCurDCVersion())));
            }
        });
    }

    private void initData(Properties props) {
        this.db = new HashMap<String, Map<String, CRDTData<?>>>();
        this.locks = new HashMap<CRDTIdentifier, LockInfo>();
        // this.notifications = new LinkedList<NotificationRecord>();
        this.modified = new HashSet<CRDTData<?>>();

        this.version = ClockFactory.newClock();
        this.cltClock = ClockFactory.newClock();

        initDB(props);

        if (dbServer.ramOnly()) {

            CRDTIdentifier id = new CRDTIdentifier("e", "1");
            ManagedCRDT<IntegerCRDT> i = new ManagedCRDT<IntegerCRDT>(id, new IntegerCRDT(id), version.clone(), true);
            localPutCRDT(i);

            CRDTIdentifier id2 = new CRDTIdentifier("e", "2");
            ManagedCRDT<IntegerCRDT> i2 = new ManagedCRDT<IntegerCRDT>(id2, new IntegerCRDT(id2), version.clone(), true);
            localPutCRDT(i2);
        }
    }

    /**********************************************************************************************
     * DATABASE FUNCTIONS
     *********************************************************************************************/
    void initDB(Properties props) {
        try {
            final DCNodeDatabase realDbServer = (DCNodeDatabase) Class.forName(
                    props.getProperty(DCConstants.DATABASE_CLASS)).newInstance();
            dbServer = new StatsNodeDatabaseWrapper(realDbServer, surrogate.siteId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot start underlying database", e);
        }
        dbServer.init(props);

    }

    CRDTData<?> readCRDTFromDB(CRDTIdentifier id) {
        return (CRDTData<?>) dbServer.read(id);
    }

    void writeCRDTintoDB(CRDTData<?> data) {

        lock(data.id);
        try {
            dbServer.write(data.id, data);
        } finally {
            unlock(data.id);
        }
    }

    /**
     * Returns database entry. If create, creates a new empty database entry. It
     * assumes that the given entry has been locked.
     * 
     * @param table
     * @param key
     * @param create
     * @return
     */
    CRDTData<?> getDatabaseEntry(CRDTIdentifier id) {
        // TODO: this synchronization could be replaced with an optimistic
        // multiton pattern + ConcurrentHashMap.putIfAbsent
        Map<String, CRDTData<?>> m;
        synchronized (db) {
            m = db.get(id.getTable());
            if (m == null) {
                m = new HashMap<String, CRDTData<?>>();
                db.put(id.getTable(), m);
            }
        }
        CRDTData<?> data = null;
        synchronized (m) {
            data = (CRDTData<?>) m.get(id.getKey());
            if (data != null)
                return data;
        }
        data = readCRDTFromDB(id);
        synchronized (m) {
            if (data == null)
                data = new CRDTData(id);
            m.put(id.getKey(), data);
            return data;
        }
    }

    private void setModifiedDatabaseEntry(CRDTData<?> crdt) {
        synchronized (modified) {
            modified.add(crdt);
        }
    }

    Endpoint resolve(CRDTIdentifier id) {
        return DHT_Node.resolveKey(id.toString());
    }

    /**
     * Executes operations in the given CRDT
     * 
     * @return returns true if the operation could be executed.
     */
    <V extends CRDT<V>> ExecCRDTResult execCRDT(CRDTObjectUpdatesGroup<V> grp, CausalityClock snapshotVersion,
            CausalityClock trxVersion, Timestamp txTs, Timestamp cltTs, Timestamp prvCltTs, CausalityClock curDCVersion) {

        Endpoint dst = DHT_Node.resolveKey(grp.getTargetUID().toString());
        if (dst == null)
            return localExecCRDT(grp, snapshotVersion, trxVersion, txTs, cltTs, prvCltTs, curDCVersion);
        else {
            return dhtRequest(dst, new DHTExecCRDT(grp, snapshotVersion, trxVersion, txTs, cltTs, prvCltTs,
                    curDCVersion));
        }
    }

    /**
     * Return null if CRDT does not exist
     * 
     * If clock equals to null, just return full CRDT
     * 
     * @param subscribe
     *            Subscription type
     * @return null if cannot fulfill request
     */
    void getCRDT(final CRDTIdentifier id, CausalityClock knownClk, CausalityClock clk, String clientId,
            boolean sendMoreRecentUpdates, boolean isSubscribed, FutureResultHandler<ManagedCRDT> rh) {
        Endpoint dst = DHT_Node.resolveKey(id.toString());
        if (dst == null) {
            rh.onResult(localGetCRDTObject(id, knownClk, clk, clientId, sendMoreRecentUpdates, isSubscribed));
        } else {
            dhtRequest(dst, new DHTGetCRDT(id, knownClk, clk, clientId, sendMoreRecentUpdates, isSubscribed), rh);
        }
    }

    /**
     * Return null if CRDT does not exist
     * 
     * If clock equals to null, just return full CRDT
     * 
     * @param subscribe
     *            Subscription type
     * @return null if cannot fulfill request
     */
    ManagedCRDT getCRDT(final CRDTIdentifier id, CausalityClock knownClk, CausalityClock clk, String clientId,
            boolean sendMoreRecentUpdates, boolean isSubscribed) {
        Endpoint dst = DHT_Node.resolveKey(id.toString());
        if (dst == null) {
            return localGetCRDTObject(id, knownClk, clk, clientId, sendMoreRecentUpdates, isSubscribed);
        } else {
            return dhtRequest(dst, new DHTGetCRDT(id, knownClk, clk, clientId, sendMoreRecentUpdates, isSubscribed));
        }
    }

    /**
     * Return null if CRDT does not exist
     */
    <V extends CRDT<V>> CRDTData<V> localPutCRDT(ManagedCRDT<V> crdt) {
        lock(crdt.getUID());
        try {
            @SuppressWarnings("unchecked")
            CRDTData<V> data = (CRDTData<V>) this.getDatabaseEntry(crdt.getUID());
            if (data.empty) {
                data.initValue(crdt, crdt.getClock(), crdt.getPruneClock(), ClockFactory.newClock());
            } else {
                // FIXME: this is an outcome of change to the op-based model and
                // discussions over e-mail.
                // It's unclear whether this should ever happen given reliable
                // DCDataServer.
                logger.warning("Unexpected concurrent put of the same object at the DCDataServer");
                // TODO: this should better be encapsulated IMHO
                data.crdt.merge(crdt);
                // if (DCDataServer.prune) {
                // data.prunedCrdt.merge(crdt);
                // }
                data.clock.merge(crdt.getClock());
                data.pruneClock.merge(crdt.getPruneClock());
                synchronized (this.cltClock) {
                    // FIXME: WHAT IS THIS??? NO-OP?
                    this.cltClock.merge(cltClock);
                }
            }
            setModifiedDatabaseEntry(data);
            return data;
        } finally {
            unlock(crdt.getUID());
        }
    }

    @SuppressWarnings("unchecked")
    <V extends CRDT<V>> ExecCRDTResult localExecCRDT(CRDTObjectUpdatesGroup<V> grp, CausalityClock _snapshotVersion,
            CausalityClock _trxVersion, Timestamp _txTs, Timestamp cltTs, Timestamp prvCltTs,
            CausalityClock curDCVersion) {
        CRDTIdentifier id = grp.getTargetUID();
        lock(id);
        try {
            CRDTData<?> data = localGetCRDT(id);
            if (data == null) {
                if (!grp.hasCreationState()) {
                    logger.warning("No creation state provided by client for an object that does not exist "
                            + grp.getTargetUID());
                    return new ExecCRDTResult(false);
                }
                V creationState = grp.getCreationState();
                // TODO: check clocks
                CausalityClock clk = grp.getDependency();
                if (clk == null) {
                    clk = ClockFactory.newClock();
                } else {
                    clk = clk.clone();
                }
                final ManagedCRDT<V> crdt = new ManagedCRDT<V>(grp.getTargetUID(), creationState, clk, true);
                // FIXME: It used to say "will merge if object exists" - not so
                // sure after the switch to op-based.
                data = localPutCRDT(crdt);
            }
            data.pruneIfPossible(pruningInterval + surrogate.timeSmootherRandom.get().nextInt(pruningInterval));

            // crdt.augumentWithScoutClock(new Timestamp(clientId, clientTxs))
            // //
            // ensures that execute() has enough information to ensure tx
            // idempotence
            // crdt.execute(updates...)
            // crdt.discardScoutClock(clientId) // critical to not polute all
            // data
            // nodes and objects with big vectors, unless we want to do it until
            // pruning

            if (prvCltTs != null)
                data.crdt.augmentWithScoutClockWithoutMappings(prvCltTs);

            // Assumption: dependencies are checked the server, prior to
            // execution...
            data.crdt.execute((CRDTObjectUpdatesGroup) grp, CRDTOperationDependencyPolicy.RECORD_BLINDLY);
            data.crdt.augmentWithDCClockWithoutMappings(curDCVersion);

            /*
             * if (DCDataServer.prune) { if (prvCltTs != null)
             * data.prunedCrdt.augmentWithScoutClock(prvCltTs);
             * data.prunedCrdt.execute((CRDTObjectUpdatesGroup) grp,
             * CRDTOperationDependencyPolicy.RECORD_BLINDLY);
             * data.prunedCrdt.augmentWithDCClock(curDCVersion);
             * data.prunedCrdt.prune(data.clock, false);
             * data.prunedCrdt.discardScoutClock(cltTs.getIdentifier());
             * data.pruneClock = data.clock; }
             */
            data.crdt.discardScoutClock(cltTs.getIdentifier());
            data.clock = data.crdt.getClock();

            setModifiedDatabaseEntry(data);

            synchronized (this.cltClock) {
                this.cltClock.recordAllUntil(cltTs);
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Data Server: for crdt : " + data.id + "; clk = " + data.clock + " ; cltClock = "
                            + cltClock + ";  snapshotVersion = " + _snapshotVersion + "; cltTs = " + cltTs);
                }
            }

            ObjectUpdatesInfo info = new ObjectUpdatesInfo(id, data.pruneClock.clone(), grp);

            dsPubSub.publish(new UpdateNotification(surrogate.surrogateId, info, surrogate.getEstimatedDCVersionCopy()));

            return new ExecCRDTResult(true, id, info);
        } finally {
            unlock(id);
        }

    }

    private ManagedCRDT localGetCRDTObject(Endpoint remote, DHTGetCRDT req) {
        if (req.subscribesUpdates())
            dsPubSub.subscribe(req.getId(), remote);
        // else
        // dsPubSub.unsubscribe(req.getId(), remote);

        return localGetCRDTObject(req.getId(), req.getKnownVersion(), req.getVersion(), req.getCltId(),
                req.sendMoreRecentUpdates(), false);
    }

    /**
     * Return null if CRDT does not exist
     * 
     * If clock equals to null, just return full CRDT
     * 
     * @param version
     * 
     * @param subscribe
     *            Subscription type
     * @return null if cannot fulfill request
     */
    ManagedCRDT localGetCRDTObject(CRDTIdentifier id, CausalityClock knownVersion, CausalityClock version,
            String clientId, boolean sendMoreRecentUpdates, boolean subscribeUpdates) {

        if (subscribeUpdates)
            dsPubSub.subscribe(id, surrogate.suPubSub);

        // else
        // dsPubSub.unsubscribe(localSurrogateId, id, suPubSub);
        lock(id);
        try {
            CRDTData<?> data = localGetCRDT(id);
            if (data == null)
                return null;

            // 1) let int clientTxs =
            // clientTxClockService.getAndLockNumberOfCommitedTxs(clientId) //
            // probably it could be done better, lock-free
            // 2)
            // let crdtCopy = retrieve(oid).copy()
            // crdtCopy.augumentWithScoutClock(new Timestamp(clientId,
            // clientTxs))
            // 3) clientTxClockService.unlock(clientId)
            // 4) return crdtCopy
            /*
             * if( DCDataServer.prune) { CMP_CLOCK cmp = version.compareTo(
             * data.pruneClock); if( cmp == CMP_CLOCK.CMP_EQUALS || cmp ==
             * CMP_CLOCK.CMP_DOMINATES) this.crdt = data.prunedCrdt.copy(); else
             * this.crdt = data.crdt.copy(); } else;
             */

            if (!sendMoreRecentUpdates && knownVersion != null
                    && !data.crdt.mayContainUpdatesBetween(knownVersion, version)) {
                return null;
            }

            // Bandwidth optimization: prune as much as possible before sending.
            // FIXME: this leaves out records covered by client clock (not in
            // "version")
            Timestamp ts = null;
            synchronized (cltClock) {
                ts = cltClock.getLatest(clientId);
            }
            final ManagedCRDT crdt = data.crdt.copyWithRestrictedVersioning(version, ts);
            // FIXME: when failing over between DCs, notifications for the
            // same update may reach the client with two different DC
            // timestamps.
            // To avoid duplicates, the notifications/fetch from a target
            // DC, need to filter out duplicates when necessary, which
            // happens only by side-effect of an implementation as of
            // 497a60a.
            if (ts != null)
                crdt.augmentWithScoutClockWithoutMappings(ts);

            return crdt;
        } finally {
            unlock(id);
        }
    }

    /**
     * Return null if CRDT does not exist
     * 
     * If clock equals to null, just return full CRDT
     * 
     * @param subscribe
     *            Subscription type
     * @return null if cannot fulfill request
     */
    CRDTData<?> localGetCRDT(CRDTIdentifier id) {
        lock(id);
        try {
            CRDTData<?> data = this.getDatabaseEntry(id);
            if (data.empty)
                return null;

            return data;
        } finally {
            unlock(id);
        }
    }

}
