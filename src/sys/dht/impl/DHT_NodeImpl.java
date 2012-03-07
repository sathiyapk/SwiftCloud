package sys.dht.impl;

import sys.net.api.rpc.RpcConnection;
import sys.net.api.rpc.RpcEndpoint;
import sys.dht.DHT_Node;
import sys.dht.api.DHT;
import sys.dht.api.DHT.Connection;
import sys.dht.catadupa.CatadupaNode;
import sys.dht.catadupa.Node;
import sys.dht.discovery.Discovery;
import sys.dht.impl.msgs.DHT_ReplyReply;
import sys.dht.impl.msgs.DHT_StubHandler;
import sys.dht.impl.msgs.DHT_RequestReply;
import sys.dht.impl.msgs.DHT_Request;

import static sys.utils.Log.*;
import static sys.net.api.Networking.*;
import static sys.dht.catadupa.Config.*;

public class DHT_NodeImpl extends CatadupaNode {

    public static final int DHT_PORT = 0;

    protected static DHT_ClientStub clientStub;
    protected static _DHT_ServerStub serverStub;

    protected DHT_NodeImpl() {
    }

    protected void init() {
        super.init();

        serverStub = new _DHT_ServerStub(new DHT.MessageHandler() {

            @Override
            public void onFailure() {
                Thread.dumpStack();
            }

            @Override
            public void onReceive(Connection conn, DHT.Key key, DHT.Message m) {
                Log.finest(String.format("Un-handled DHT message [<%s,%s>]", key, m.getClass()));
            }
        });
        Discovery.register(DHT_Node.DHT_ENDPOINT, serverStub.getEndpoint().localEndpoint());

        clientStub = new _DHT_ClientStub(serverStub.getEndpoint());
    }

    @Override
    public void onNodeAdded(Node n) {
        Log.finest("Catadupa added:" + n);
    }

    @Override
    public void onNodeRemoved(Node n) {
        Log.finest("Catadupa removed:" + n);
    }

    private Node resolve(final DHT.Key key) {
        long key2key = key.longHashValue() % Config.NODE_KEY_LENGTH;
        for (Node i : super.db.nodes(key2key))
            if (i.isOnline())
                return i;

        return self;
    }

    protected class _DHT_ClientStub extends DHT_ClientStub {

        _DHT_ClientStub(RpcEndpoint myEndpoint) {
            super(myEndpoint, myEndpoint.localEndpoint());
        }

        @Override
        public void send(DHT.Key key, DHT.Message msg) {
            Node nextHop = resolve(key);
            if (nextHop != null) {
                myEndpoint.send(nextHop.endpoint, new DHT_Request(key, msg));
            } else {
                Thread.dumpStack();
            }
        }

        @Override
        public void send(DHT.Key key, DHT.Message msg, DHT.ReplyHandler handler) {
            Node nextHop = resolve(key);
            if (nextHop != null) {
                myEndpoint.send(nextHop.endpoint, new DHT_Request(key, msg, new DHT_PendingReply(handler).handlerId, null));
            } else {
                Thread.dumpStack();
            }
        }
    }

    protected class _DHT_ServerStub extends DHT_StubHandler {

        DHT.MessageHandler myHandler;
        final RpcEndpoint myEndpoint;

        _DHT_ServerStub(DHT.MessageHandler myHandler) {
            this.myHandler = myHandler;
            this.myEndpoint = Networking.rpcBind(DHT_PORT, this);
        }

        RpcEndpoint getEndpoint() {
            return myEndpoint;
        }

        public void setHandler(DHT.MessageHandler handler) {
            this.myHandler = handler;
        }

        public void onReceive(RpcConnection conn, DHT_Request req) {
            Node nextHop = resolve(req.key);
            if (nextHop != null && nextHop.key != self.key) {
                myEndpoint.send(nextHop.endpoint, req);
            } else {
                req.payload.deliverTo(new DHT_ConnectionImpl(conn, req.handlerId, myEndpoint, req.srcEndpoint), req.key, myHandler);
            }
        }

        public void onReceive(RpcConnection conn, DHT_RequestReply reply) {
            DHT_PendingReply prh = DHT_PendingReply.getHandler(reply.handlerId);
            if (prh != null) {
                reply.payload.deliverTo(new DHT_ConnectionImpl(conn, reply.replyHandlerId), prh.handler);
            } else {
                Thread.dumpStack();
            }
        }
        
        public void onReceive(RpcConnection conn, DHT_ReplyReply reply) {
            DHT_PendingReply prh = DHT_PendingReply.getHandler(reply.handlerId);
            if (prh != null) {
                reply.payload.deliverTo(new DHT_ConnectionImpl(conn, reply.replyHandlerId), prh.handler);
            } else {
                Thread.dumpStack();
            }
        }
    }

}