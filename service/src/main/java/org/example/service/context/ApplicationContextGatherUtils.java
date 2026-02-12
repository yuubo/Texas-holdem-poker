package org.example.service.context;

import org.springframework.context.ApplicationContext;

/**
 * spring完成启动后才能使用
 */
public class ApplicationContextGatherUtils {

    static NettyApplicationContext nettyApplicationContext;

    static ApplicationContext applicationContext;

    public static PokerContext pokerContext;

    public static NettyApplicationContext nettyApplicationContext() {
        return nettyApplicationContext;
    }

    public static ApplicationContext applicationContext() {
        return applicationContext;
    }

    public static PokerContext pokerContext() {
        return pokerContext;
    }
}
