package org.example.common.factory;

import io.netty.channel.nio.NioEventLoopGroup;

public class NioEventLoopGroupFactory implements EventLoopGroupFactory{
    @Override
    public NioEventLoopGroup create() {
        return new NioEventLoopGroup();
    }

    @Override
    public NioEventLoopGroup create(int nThreads) {
        return new NioEventLoopGroup(nThreads);
    }
}
