package org.example.common.init;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import org.example.common.constant.CommonConstant;
import org.example.common.factory.EpollEventLoopGroupFactory;
import org.example.common.factory.EventLoopGroupFactory;
import org.example.common.factory.NioEventLoopGroupFactory;
import org.example.common.handle.BoJsonOutboundHandle;
import org.example.common.handle.Delimiter;
import org.example.common.handle.JsonToBOInboundHandle;
import org.example.common.handle.MessageJsonProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Configuration
@Import({
        BoJsonOutboundHandle.class,
        JsonToBOInboundHandle.class
})
public class CommonAutoConfiguration {

    @Bean
    public EventLoopGroupFactory eventLoopGroupFactory(@Value("${netty.service.eventLoop.type:NIO}") String eventLoopType,
                                                       @Autowired(required = false) EventExecutorChooserFactory eventExecutorChooserFactory) {
        if (eventExecutorChooserFactory == null) {
            eventExecutorChooserFactory = DefaultEventExecutorChooserFactory.INSTANCE;
        }

        if ("EPOLL".equals(eventLoopType)) {
            return new EpollEventLoopGroupFactory(eventExecutorChooserFactory);
        }else {
            return new NioEventLoopGroupFactory(eventExecutorChooserFactory);
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
    public MessageSource messageSource(Locale locale) {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        source.setDefaultLocale(locale);

        MessageSourceUtils.messageSource = source;

        return source;
    }

    @Bean
    public Delimiter delimiter() {
        int maxLength = 1024 * 1024;
        ByteBuf byteBuf = Unpooled.copiedBuffer(CommonConstant.MESSAGE_END_MARK.getBytes(StandardCharsets.UTF_8));
        return new Delimiter(byteBuf, maxLength);
    }

    @Bean
    @Scope("prototype")
    public DelimiterBasedFrameDecoder delimiterBasedFrameDecoder(Delimiter delimiter) {
        return new DelimiterBasedFrameDecoder(delimiter.maxLength(), delimiter.messageEndMark());
    }

    @Bean
    @Scope("prototype")
    public StringDecoder stringDecoder() {
        return new StringDecoder(StandardCharsets.UTF_8);
    }

    @Bean
    @Scope("prototype")
    public StringEncoder stringEncoder() {
        return new StringEncoder(StandardCharsets.UTF_8);
    }

    @Bean
    @ConditionalOnMissingBean(MessageJsonProcessor.class)
    public MessageJsonProcessor messageJsonProcessor() {
        return new MessageJsonProcessor() {};
    }

}
