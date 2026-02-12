package org.example.service.context;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import jakarta.annotation.PostConstruct;
import org.example.common.message.User;
import org.example.common.utils.UserNameUtil;
import org.example.service.channel.PokerChannel;
import org.example.service.channel.PokerChannelStatusEnum;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class NettyApplicationContext implements ApplicationRunner, DisposableBean {

    private volatile Map<EventLoop, LinkedHashMap<Channel, PokerChannel>> pokerChannelMap = new HashMap<>();
    private volatile List<Channel> noCheckList = new ArrayList<>();

    private ReentrantLock lock = new ReentrantLock();

    private static final int MAX_SIZE = 100;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ServerBootstrap serverBootstrap;

    @Autowired
    private Bootstrap bootstrap;

    @Value("${netty.service.tcp.port:8081}")
    private int tcpPort;

    @Value("${netty.service.udp.port:8080}")
    private int udpPort;

    private ChannelFuture tcpChannelFuture;

    private ChannelFuture udpChannelFuture;

    @Value("${netty.service.udp.enabled:true}")
    private boolean isUdpEnabled;

    @PostConstruct
    public void init() {
        ApplicationContextGatherUtils.applicationContext = applicationContext;
        ApplicationContextGatherUtils.nettyApplicationContext = this;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tcpChannelFuture = serverBootstrap.bind(tcpPort).sync();
        System.out.println("启动TCP: "+tcpPort);
        if (isUdpEnabled) {
            udpChannelFuture = bootstrap.bind(udpPort).sync();
            System.out.println("启动UDP: "+udpPort);
        }
    }

    public List<PokerChannel> getPokerChannel(EventLoop eventLoop) {
        return pokerChannelMap.get(eventLoop).values().stream().toList();
    }

    public PokerChannel addPokerChanne(Channel context) {

        Object add = add(pokerChannelMap, context);
        if (add != null && add instanceof PokerChannel) {
            return (PokerChannel) add;
        }
        return null;
    }

    public PokerChannel removePokerChannel(Channel channel) {
        return (PokerChannel) remove(pokerChannelMap, channel);
    }

    public boolean noCheckContains(Channel channel) {
        return noCheckList.contains(channel);
    }

    public boolean addNoCheckList(Channel channel) {
        return (boolean) add(noCheckList, channel);
    }

    public boolean removeNoCheckList(Channel channel) {
        return (boolean) remove(noCheckList, channel);
    }

    private Object add(Object object, Channel channel) {
        lock.lock();
        try {
            Object add = null;
            if (this.pokerChannelMap.size() >= MAX_SIZE) {
                add = false;
            } else {
                if (object == this.pokerChannelMap) {
                    String userName = UserNameUtil.getUserName();
                    add = new PokerChannel(channel, new User(userName));
                    LinkedHashMap<Channel, PokerChannel> map = pokerChannelMap.get(channel.eventLoop());
                    if (map == null) {
                        map = new LinkedHashMap<>();
                        pokerChannelMap.put(channel.eventLoop(), map);
                    }
                    map.put(channel, (PokerChannel) add);
                } else {
                    noCheckList.add(channel);
                    add = true;
                }
            }
            return add;
        } finally {
            lock.unlock();
        }
    }

    private Object remove(Object object, Channel channel) {
        lock.lock();
        try {
            Object remove = null;
            if (object == this.pokerChannelMap) {
                Map<Channel, PokerChannel> map = this.pokerChannelMap.get(channel.eventLoop());
                PokerChannel pokerChannel = map.get(channel);
                map.remove(channel);
                return pokerChannel;
            } else {
                remove = this.noCheckList.remove(channel);
            }

            return remove;

        } finally {
            lock.unlock();
        }
    }

    public PokerChannel getPokerChannel(Channel channel) {
        Map<Channel, PokerChannel> map = pokerChannelMap.get(channel.eventLoop());
        return map == null ? null : map.get(channel);
    }

    @Override
    public void destroy() throws Exception {
        serverBootstrap.config().group().shutdownGracefully();
        serverBootstrap.config().childGroup().shutdownGracefully();
    }

}
