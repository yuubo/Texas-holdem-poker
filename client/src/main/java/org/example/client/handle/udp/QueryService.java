package org.example.client.handle.udp;

import io.netty.channel.Channel;
import org.example.common.warp.UdpMessage;

import java.net.InetSocketAddress;

public class QueryService implements Runnable {

    private static QueryService queryService = null;

    private UdpMessage searchUdpMessage = new UdpMessage();

    private static volatile int searchFrequency = 0;

    private Channel ch;

    private QueryService(Channel ch) {
        searchUdpMessage.setSocketAddress(new InetSocketAddress("255.255.255.255", 8080));
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

    public static synchronized QueryService queryServiceFactory(Channel ch) {
        if (queryService == null) {
            queryService = new QueryService(ch);
        }
        return queryService;
    }

}
