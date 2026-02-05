package org.example.common.init;

import org.example.common.factory.EpollEventLoopGroupFactory;
import org.example.common.factory.EventLoopGroupFactory;
import org.example.common.factory.NioEventLoopGroupFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class CommonAutoConfiguration {

    @Bean
    public EventLoopGroupFactory eventLoopGroupFactory(@Value("${netty.service.eventLoop.type:NIO}") String eventLoopType) {
        if ("EPOLL".equals(eventLoopType)) {
            return new EpollEventLoopGroupFactory();
        }else {
            return new NioEventLoopGroupFactory();
        }
    }

    @Bean
    public Locale localeResolver(@Value("${netty.languageCountry:zh_CN}") String languageCountry) {
        String[] lcArray = languageCountry.split("_");
        Locale locale = new Locale(lcArray[0], lcArray[1]);
        MessageSourceUtils.locale = locale;
        return locale;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("classpath:messages"); // 注意文件名不包括后缀，Spring会自动加上 .properties 后缀并查找不同语言的版本
        source.setDefaultEncoding("UTF-8"); // 设置编码方式为UTF-8

        MessageSourceUtils.messageSource = source;

        return source;
    }

}
