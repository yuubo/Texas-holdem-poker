package org.example.client.handle.udp;

import io.netty.channel.Channel;
import org.example.common.warp.UdpMessage;

import java.net.InetSocketAddress;

public class QueryService implements Runnable {

    private static QueryService queryService = null;

    private UdpMessage searchUdpMessage = new UdpMessage();

    private static volatile int searchFrequency = 0;

    private Channel ch;


    private QueryService(Channel ch, String udpHost, int udpPort) {
        searchUdpMessage.setSocketAddress(new InetSocketAddress(udpHost, udpPort));
        searchUdpMessage.setMessage("hello");
        this.ch = ch;
    }

    public static int getSearchFrequency() {
        return searchFrequency;
    }

    @Override
    public void run() {
        searchFrequency++;
        /*while (searchFrequency++ < 5) {
            try {
                System.out.println("开始搜索服务" + searchFrequency);
                ch.writeAndFlush(searchUdpMessage);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("开始搜索服务" + searchFrequency);
        ch.writeAndFlush(searchUdpMessage);
    }

    public static synchronized QueryService queryServiceFactory(Channel ch, String udpHost, int udpPort) {
        if (queryService == null) {
            queryService = new QueryService(ch, udpHost, udpPort);
        }
        return queryService;
    }

    public static QueryService queryService() {
        if (queryService == null) {
            throw new RuntimeException("请先调用queryServiceFactory方法");
        }
        return queryService;
    }

}
