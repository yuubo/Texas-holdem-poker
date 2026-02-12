package org.example.common.factory;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandlers;

public class EpollEventLoopGroupFactory implements EventLoopGroupFactory {

    private EventExecutorChooserFactory eventExecutorChooserFactory;

    public EpollEventLoopGroupFactory(EventExecutorChooserFactory eventExecutorChooserFactory) {
        this.eventExecutorChooserFactory = eventExecutorChooserFactory;
    }

    @Override
    public EpollEventLoopGroup create() {
        return create(0);
    }

    @Override
    public EpollEventLoopGroup create(int nThreads) {
        //return new EpollEventLoopGroup(nThreads);
        return new EpollEventLoopGroup(
                nThreads,
                null,
                eventExecutorChooserFactory,
                DefaultSelectStrategyFactory.INSTANCE,
                RejectedExecutionHandlers.reject()
        );
    }
}
