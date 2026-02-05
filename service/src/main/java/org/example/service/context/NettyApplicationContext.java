package org.example.service.context;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import jakarta.annotation.PostConstruct;
import org.example.common.bo.User;
import org.example.common.channel.PokerChannel;
import org.example.common.utils.UserNameUtil;
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

    private volatile Map<Channel, PokerChannel> pokerChannelMap = new LinkedHashMap<>();
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

    @PostConstruct
    public void init() {
        ApplicationContextGatherUtils.applicationContext = applicationContext;
        ApplicationContextGatherUtils.nettyApplicationContext = this;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tcpChannelFuture = serverBootstrap.bind(tcpPort).sync();
        System.out.println("启动TCP: "+tcpPort);
        udpChannelFuture = bootstrap.bind(udpPort).sync();
        System.out.println("启动UDP: "+udpPort);
    }

    public List<PokerChannel> getPokerChannel() {
        return pokerChannelMap.values().stream().toList();
    }

    public PokerChannel addPokerChanne(Channel context) {

        Object add = add(pokerChannelMap, context);
        if (add != null && add instanceof PokerChannel) {
            return (PokerChannel) add;
        }
        return null;
    }

    public PokerChannel removePokerChannel(Channel context) {
        return (PokerChannel) remove(pokerChannelMap, context);
    }

    public boolean noCheckContains(Channel context) {
        return noCheckList.contains(context);
    }

    public boolean addNoCheckList(Channel context) {
        return (boolean) add(noCheckList, context);
    }

    public boolean removeNoCheckList(Channel context) {
        return (boolean) remove(noCheckList, context);
    }

    private Object add(Object object, Channel context) {
        lock.lock();
        Object add = null;
        if (this.pokerChannelMap.size() >= MAX_SIZE) {
            add = false;
        } else {
            if (object == this.pokerChannelMap) {
                add = new PokerChannel(context, new User(UserNameUtil.getUserName()));
                pokerChannelMap.put(context, (PokerChannel) add);
            } else {
                noCheckList.add(context);
                add = true;
            }
        }
        lock.unlock();
        return add;
    }

    private Object remove(Object object, Channel context) {
        lock.lock();
        Object remove = null;
        if (object == this.pokerChannelMap) {
            this.pokerChannelMap.remove(context);
        } else {
            remove = this.noCheckList.remove(context);
        }
        lock.unlock();
        return remove;
    }

    public PokerChannel getPokerChannel(Channel channel) {
        return pokerChannelMap.get(channel);
    }

    @Override
    public void destroy() throws Exception {
        serverBootstrap.config().group().shutdownGracefully();
        serverBootstrap.config().childGroup().shutdownGracefully();
    }

}
