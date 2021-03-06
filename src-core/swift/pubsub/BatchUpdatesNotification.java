package swift.pubsub;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import swift.clocks.CausalityClock;
import swift.crdt.core.CRDTIdentifier;
import swift.crdt.core.CRDTObjectUpdatesGroup;
import swift.crdt.core.CRDTUpdate;
import swift.proto.MetadataSamplable;
import swift.proto.MetadataStatsCollector;
import swift.proto.SwiftProtocolHandler;
import sys.net.api.rpc.RpcHandle;
import sys.net.api.rpc.RpcHandler;
import sys.pubsub.PubSub.Subscriber;
import sys.pubsub.PubSubNotification;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * A notification that brings client's cache (a set of subscribed objects) from
 * the previous consistent state to the new one, described by a vector.
 * 
 * @author mzawirski,smd
 */
public class BatchUpdatesNotification extends PubSubNotification<CRDTIdentifier> implements MetadataSamplable {

    protected CausalityClock newVersion;
    private boolean newVersionDisasterSafe;
    protected Map<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> objectsUpdates;
    // THIS IS USED FOR EXPERIMENT PURPOSES ONLY: to measure the overhead of
    // PRACTI/Depot metadata
    protected CausalityClock fakePractiDepotVector;

    BatchUpdatesNotification() {
    }

    public BatchUpdatesNotification(CausalityClock newVersion, boolean disasterSafe,
            Map<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> objectsUpdates, CausalityClock newFakeVector) {
        this.newVersion = newVersion;
        this.newVersionDisasterSafe = disasterSafe;
        this.objectsUpdates = objectsUpdates;
        this.fakePractiDepotVector = newFakeVector;
    }

    public BatchUpdatesNotification(CausalityClock newVersion, boolean disasterSafe,
            Map<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> objectsUpdates) {
        this(newVersion, disasterSafe, objectsUpdates, null);
    }

    // @Override
    // public void notifyTo(PubSub<CRDTIdentifier> pubsub) {
    // ((SwiftSubscriber) pubsub).onNotification(this);
    // }

    /**
     * @return new consistent version that these packet updates client's cache
     *         to
     */
    public CausalityClock getNewVersion() {
        return newVersion;
    }

    /**
     * @return a map of all updates on the objects subscribed by the client,
     *         with timestamps between {@link #getNewVersion()} of the previous
     *         notification, and {@link #getNewVersion()} of this one
     */
    // FIXME: define subscribed objects with an epochId or so?
    public Map<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> getObjectsUpdates() {
        return objectsUpdates;
    }

    /**
     * @return true if the version is disaster safe
     */
    public boolean isNewVersionDisasterSafe() {
        return newVersionDisasterSafe;
    }

    @Override
    public Object src() {
        return null;
    }

    @Override
    public CRDTIdentifier key() {
        return null;
    }

    @Override
    public Set<CRDTIdentifier> keys() {
        return Collections.unmodifiableSet(objectsUpdates.keySet());
    }

    @Override
    public void recordMetadataSample(MetadataStatsCollector collector) {
        if (!collector.isMessageReportEnabled()) {
            return;
        }
        Kryo kryo = collector.getFreshKryo();
        Output buffer = collector.getFreshKryoBuffer();

        // TODO: get it from the wire, rather than recompute
        kryo.writeObject(buffer, this);
        // FIXME: repsect fakePractiDepotVector != null
        final int totalSize = buffer.position();

        kryo = collector.getFreshKryo();
        buffer = collector.getFreshKryoBuffer();
        if (fakePractiDepotVector != null) {
            kryo.writeObject(buffer, fakePractiDepotVector);
        } else {
            kryo.writeObject(buffer, newVersion);
        }
        kryo.writeObject(buffer, newVersionDisasterSafe);
        final int batchIndependentGlobalMetadata = buffer.position();

        kryo = collector.getFreshKryo();
        buffer = collector.getFreshKryoBuffer();
        for (final Entry<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> entry : objectsUpdates.entrySet()) {
            for (final CRDTObjectUpdatesGroup<?> group : entry.getValue()) {
                if (fakePractiDepotVector != null) {
                    kryo.writeObject(buffer, group.getClientTimestamp());
                } else {
                    kryo.writeObject(buffer, group.getTimestampMapping());
                }
            }
        }
        final int batchDependentGlobalMetadata = buffer.position();

        kryo = collector.getFreshKryo();
        buffer = collector.getFreshKryoBuffer();
        int numberOfTxns = 0;
        int numberOfGroups = 0;
        int numberOfOps = 0;
        for (final Entry<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> entry : objectsUpdates.entrySet()) {
            kryo.writeObject(buffer, entry.getKey());
            for (final CRDTObjectUpdatesGroup<?> group : entry.getValue()) {
                if (group.hasCreationState()) {
                    kryo.writeObject(buffer, group.getCreationState());
                }
                kryo.writeObject(buffer, group.getOperations());
            }
        }
        final int updatesSize = buffer.position();

        kryo = collector.getFreshKryo();
        buffer = collector.getFreshKryoBuffer();
        for (final Entry<CRDTIdentifier, List<CRDTObjectUpdatesGroup<?>>> entry : objectsUpdates.entrySet()) {
            numberOfTxns++;
            kryo.writeObject(buffer, entry.getKey());
            for (final CRDTObjectUpdatesGroup<?> group : entry.getValue()) {
                numberOfGroups++;
                if (group.hasCreationState()) {
                    final Object value = group.getCreationState().getValue();
                    if (value != null) {
                        kryo.writeObject(buffer, value);
                    } else {
                        kryo.writeObject(buffer, false);
                    }
                }
                for (final CRDTUpdate<?> op : group.getOperations()) {
                    numberOfOps++;
                    final Object value = op.getValueWithoutMetadata();
                    if (value != null) {
                        kryo.writeObject(buffer, value);
                    } else {
                        kryo.writeObject(buffer, false);
                    }
                }
            }
        }
        final int valuesSize = buffer.position();

        final int vectorSize = Math.max(newVersion.getSize(),
                fakePractiDepotVector != null ? fakePractiDepotVector.getSize() : 0);
        final int maxExceptionsNum = newVersion.getExceptionsNumber();
        collector.recordMessageStats(this, totalSize, updatesSize, valuesSize, batchIndependentGlobalMetadata,
                batchDependentGlobalMetadata, numberOfOps, numberOfGroups, numberOfTxns, vectorSize, maxExceptionsNum);
    }

    @Override
    public String toString() {
        return "BatchUpdatesNotification [newVersion=" + newVersion + ", newVersionDisasterSafe="
                + newVersionDisasterSafe + ", objectsUpdates=" + objectsUpdates + "]";
    }

    @Override
    public void notifyTo(Subscriber<CRDTIdentifier> subscriber) {
        ((SwiftSubscriber) subscriber).onNotification(this);
    }

    @Override
    public void deliverTo(RpcHandle handle, RpcHandler handler) {
        ((SwiftProtocolHandler) handler).onReceive(this);
    }
}
