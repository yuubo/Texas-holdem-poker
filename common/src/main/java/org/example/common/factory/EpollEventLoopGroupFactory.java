package org.example.common.factory;

import io.netty.channel.epoll.EpollEventLoopGroup;

public class EpollEventLoopGroupFactory implements EventLoopGroupFactory {

    @Override
    public EpollEventLoopGroup create() {
        return new EpollEventLoopGroup();
    }

    @Override
    public EpollEventLoopGroup create(int nThreads) {
        return new EpollEventLoopGroup(nThreads);
    }
}
