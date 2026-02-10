package org.example.common.factory;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutorChooserFactory;

import java.nio.channels.spi.SelectorProvider;

public class NioEventLoopGroupFactory implements EventLoopGroupFactory {
    private EventExecutorChooserFactory eventExecutorChooserFactory;

    public NioEventLoopGroupFactory(EventExecutorChooserFactory eventExecutorChooserFactory) {
        this.eventExecutorChooserFactory = eventExecutorChooserFactory;
    }

    @Override
    public NioEventLoopGroup create() {
        return create(0);
    }

    @Override
    public NioEventLoopGroup create(int nThreads) {
        return new NioEventLoopGroup(
                nThreads,
                null,
                eventExecutorChooserFactory,
                SelectorProvider.provider(),
                DefaultSelectStrategyFactory.INSTANCE);
    }
}
