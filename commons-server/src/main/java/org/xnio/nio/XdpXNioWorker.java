package org.xnio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.ManagementRegistration;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;

public class XdpXNioWorker extends XnioWorker {
    private final NioXnioWorker delegation;

    public XdpXNioWorker(Builder builder) {
        super(builder);
        this.delegation = new NioXnioWorker(builder);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegation.awaitTermination(timeout, unit);
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        delegation.awaitTermination();
    }

    @Override
    public XnioIoThread getIoThread(int hashCode) {
        return delegation.getIoThread(hashCode);
    }

    @Override
    public int getIoThreadCount() {
        return delegation.getIoThreadCount();
    }

    @Override
    protected XnioIoThread chooseThread() {
        return delegation.chooseThread();
    }

    @Override
    public XnioWorkerMXBean getMXBean() {
        return delegation.getMXBean();
    }

    @Override
    protected ManagementRegistration registerServerMXBean(XnioServerMXBean metrics) {
        return delegation.registerServerMXBean(metrics);
    }

    @Override
    public void shutdown() {
        delegation.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegation.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegation.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegation.isTerminated();
    }

    public void start() {
        delegation.start();
    }

    protected AcceptingChannel<StreamConnection> createTcpConnectionServer(final InetSocketAddress bindAddress, final ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, final OptionMap optionMap) throws IOException {
        boolean ok = false;
        final ServerSocketChannel channel = ServerSocketChannel.open();
        try {
            if (optionMap.contains(Options.RECEIVE_BUFFER)) {
                channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));
            }

            channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
            channel.configureBlocking(false);

            if (optionMap.contains(Options.BACKLOG)) {
                channel.socket().bind(bindAddress, optionMap.get(Options.BACKLOG, 128));
            } else {
                channel.socket().bind(bindAddress);
            }

            final QueuedNioTcpServer2 server = new QueuedNioTcpServer2(new NioTcpServer(delegation, channel, optionMap, true));
            server.setAcceptListener(acceptListener);
            ok = true;
            return server;
        } finally {
            if (!ok) {
                IoUtils.safeClose(channel);
            }
        }
    }

    public static XdpXNioWorker createWorker(int numIOThreads, int numWorkerThreads) {
        Builder builder = new NioXnioProvider().getInstance().createWorkerBuilder()
                                               .populateFromOptions(OptionMap.builder()
                                                                             .set(Options.WORKER_IO_THREADS, numIOThreads)
                                                                             .set(Options.CONNECTION_HIGH_WATER, 1000000)
                                                                             .set(Options.CONNECTION_LOW_WATER, 1000000)
                                                                             .set(Options.WORKER_TASK_CORE_THREADS, numWorkerThreads)
                                                                             .set(Options.WORKER_TASK_MAX_THREADS, numWorkerThreads)
                                                                             .set(Options.TCP_NODELAY, true)
                                                                             .set(Options.CORK, true).getMap());

        XdpXNioWorker worker = new XdpXNioWorker(builder);
        worker.start();

        return worker;
    }
}
