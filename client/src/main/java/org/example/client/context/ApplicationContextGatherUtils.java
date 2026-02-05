package org.example.client.context;

import org.springframework.context.ApplicationContext;

/**
 * spring完成启动后才能使用
 */
public class ApplicationContextGatherUtils {

    static ClientApplicationContext clientApplicationContext;

    static ApplicationContext applicationContext;

    public static ClientApplicationContext clientApplicationContext() {
        return clientApplicationContext;
    }

    public static ApplicationContext applicationContext() {
        return applicationContext;
    }
}
