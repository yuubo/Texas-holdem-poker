package org.example.service.factory;

import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PokerEventExecutorChooserFactory implements EventExecutorChooserFactory {

    @Value("${netty.service.eventLoop.channel.quantity:8}")
    private int quantity;

    @Override
    public EventExecutorChooser newChooser(EventExecutor[] executors) {
        return new PokerEventExecutorChooser(executors, quantity);
    }

    public class PokerEventExecutorChooser implements EventExecutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private final EventExecutor[] executors;
        private final ReentrantLock lock = new ReentrantLock();

        PokerEventExecutorChooser(EventExecutor[] executors, int quantity) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            lock.lock();
            try {
                //从第1个EventLoop开始，每个EventLoop分配quantity个连接
                for (int i = 0; i < executors.length; i++) {
                    SingleThreadEventLoop e = (SingleThreadEventLoop) executors[idx.get()];
                    if (e.registeredChannels() <  quantity) {
                        return e;
                    } else {
                        idx.incrementAndGet();
                    }
                }
                throw new RuntimeException("服务器连接已满");
            } finally {
                lock.unlock();
            }

        }
    }
}
