package org.example.common.factory;

import io.netty.channel.EventLoopGroup;

public interface EventLoopGroupFactory {
    EventLoopGroup create();
    EventLoopGroup create(int nThreads);
}
