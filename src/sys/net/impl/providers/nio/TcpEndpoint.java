package sys.net.impl.providers.nio;

import static sys.utils.Log.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static sys.Sys.*;
import sys.net.api.Endpoint;
import sys.net.api.Message;
import sys.net.api.TransportConnection;
import sys.net.impl.AbstractEndpoint;
import sys.net.impl.AbstractLocalEndpoint;
import sys.net.impl.FailedTransportConnection;
import sys.net.impl.providers.AbstractTransport;
import sys.net.impl.providers.KryoBuffer;
import sys.net.impl.providers.KryoBufferPool;
import sys.net.impl.providers.LocalEndpointExchange;
import sys.net.impl.providers.RemoteEndpointUpdater;
import sys.utils.IO;
import sys.utils.Threading;

public class TcpEndpoint extends AbstractLocalEndpoint implements Runnable {

	private static final int MAX_POOL_THREADS = 12;
	private static final int CORE_POOL_THREADS = 8;
	private static final int MAX_IDLE_THREAD_IMEOUT = 30;
	final BlockingQueue<Runnable> holdQueue = new ArrayBlockingQueue<Runnable>(128);
	final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_THREADS, MAX_POOL_THREADS, MAX_IDLE_THREAD_IMEOUT, TimeUnit.SECONDS, holdQueue);

	ServerSocketChannel ssc;
	final KryoBufferPool writePool;

	public TcpEndpoint(Endpoint local, int tcpPort) throws IOException {
		this.localEndpoint = local;
		this.gid = Sys.rg.nextLong();

		if (tcpPort >= 0) {
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(tcpPort));
		}
		super.setSocketAddress(ssc == null ? 0 : ssc.socket().getLocalPort());
		writePool = new KryoBufferPool(ssc != null ? 16 : 4);
	}

	public void start() throws IOException {

		handler = localEndpoint.getHandler();
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		while (this.writePool.remainingCapacity() > 0)
			this.writePool.offer(new KryoBuffer());

		if (ssc != null)
			Threading.newThread(true, this).start();
	}

	public TransportConnection connect(Endpoint remote) {
		try {
			if (((AbstractEndpoint) remote).isIncoming())
				return new OutgoingConnection(remote);
			else
				Log.severe("Attempting to connect to an outgoing only endpoint" + remote);
			return new FailedTransportConnection(localEndpoint, remote, null);
		} catch (Throwable t) {
			t.printStackTrace();
			Log.severe("Cannot connect to: " + remote + " :" + t.getMessage());
			return new FailedTransportConnection(localEndpoint, remote, t);
		}
	}

	@Override
	public void run() {
		try {
			Log.finest("Bound to: " + this);
			for (;;) {
				SocketChannel channel = ssc.accept();
				channel.socket().setTcpNoDelay(true);
				new IncomingConnection(channel);
			}
		} catch (Exception x) {
			x.printStackTrace();
			Log.severe("Unexpected error in incoming endpoint: " + localEndpoint);
		}
		IO.close(ssc);
	}

	abstract class AbstractConnection extends AbstractTransport implements RemoteEndpointUpdater, Runnable {

		Throwable cause;
		SocketChannel channel;
		final KryoBufferPool readPool;

		public AbstractConnection() throws IOException {
			super(localEndpoint, null);
			this.readPool = new KryoBufferPool();
		}

		@Override
		public void run() {

			while (this.readPool.remainingCapacity() > 0)
				this.readPool.offer(new _ReadBuffer());

			try {
				for (;;) {

					KryoBuffer inBuf = readPool.take();

					if (inBuf == null)
						inBuf = new _ReadBuffer();

					if (inBuf.readFrom(channel)) {

						inBuf.run();
						//threadPool.execute(inBuf);
					} else {
						this.readPool.offer(inBuf);
						break;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				cause = t;
			}
			isBroken = true;
			IO.close(channel);
			handler.onFailure(this);
			Log.fine("Closed connection to:" + remote);
		}

		class _ReadBuffer extends KryoBuffer {
			@Override
			public void run() {
				Message msg;
				try {
					msg = super.readClassAndObject();
				} catch (Throwable t) {
					t.printStackTrace();
					return;
				}
				readPool.offer(this);
				msg.deliverTo(AbstractConnection.this, TcpEndpoint.this.handler);
			}
		}

		public boolean send(final Message m) {
			KryoBuffer outBuf = null;
			try {
				outBuf = writePool.take();
				if (outBuf == null)
					outBuf = new KryoBuffer();

				outBuf.writeClassAndObjectFrame(m, channel);
				return true;
			} catch (Throwable t) {
				cause = t;
				isBroken = true;
				IO.close(channel);
				handler.onFailure(this);
			} finally {
				if (outBuf != null)
					writePool.offer(outBuf);
			}
			return false;
		}

		public <T extends Message> T receive() {
			throw new RuntimeException("Not supported...");
			// KryoBuffer inBuf = null;
			// try {
			// inBuf = rq.take();
			// T msg = inBuf.readClassAndObject();
			// return msg;
			//
			// } catch (Throwable t) {
			// t.printStackTrace();
			// } finally {
			// if (inBuf != null)
			// readPool.offer(inBuf);
			// }
			// return null;
		}

		@Override
		public Throwable causeOfFailure() {
			return failed() ? cause : null;
		}
	}

	class IncomingConnection extends AbstractConnection {

		public IncomingConnection(SocketChannel channel) throws IOException {
			super.channel = channel;
			Threading.newThread(true, this).start();
		}
	}

	class OutgoingConnection extends AbstractConnection implements Runnable {
		final private int CONNECTION_TIMEOUT = 5000;

		public OutgoingConnection(Endpoint remote) throws IOException {
			super.setRemoteEndpoint(remote);
			init();
		}

		void init() throws IOException {
			try {
				channel = SocketChannel.open();
				channel.socket().connect(((AbstractEndpoint) remote).sockAddress(), CONNECTION_TIMEOUT);
				channel.socket().setTcpNoDelay(true);
			} catch (IOException x) {
				x.printStackTrace();
				
				cause = x;
				isBroken = true;
				IO.close(channel);
				throw x;
			}
			this.send(new LocalEndpointExchange(localEndpoint));
			handler.onConnect(this);
			Threading.newThread(true, this).start();
		}

		public String toString() {
			return "" + super.hashCode();
		}
	}
}