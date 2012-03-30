package swift.client.proto;

import swift.clocks.CausalityClock;
import sys.net.api.rpc.RpcConnection;
import sys.net.api.rpc.RpcHandler;
import sys.net.api.rpc.RpcMessage;

/**
 * Client request for recent updates on previously subscribed objects (see
 * {@link FetchObjectVersionRequest#isSubscribeUpdatesRequest()}. Beside of
 * requesting recent updates, this request keep client subscriptions alive.
 * <p>
 * This call may block until new updates arrive, a timeout elapses on the server
 * (to inform of no updates) or server confirms a new subscription. Client
 * should repeatedly send this request in order to get new updates and keep her
 * subscriptions alive.
 * 
 * @author mzawirski
 */
public class RecentUpdatesRequest implements RpcMessage {
    protected CausalityClock lastClock;

    /**
     * No-args constructor for Kryo-serialization.
     */
    public RecentUpdatesRequest() {
    }

    public RecentUpdatesRequest(final CausalityClock lastClock) {
        this.lastClock = lastClock;
    }

    /**
     * @return clock of previously received {@link RecentUpdatesReply}; null if
     *         client starts a new session without relying on previous messages
     */
    public CausalityClock getLastClock() {
        return lastClock;
    }

    @Override
    public void deliverTo(RpcConnection conn, RpcHandler handler) {
        ((SwiftServer) handler).onReceive(conn, this);
    }
}