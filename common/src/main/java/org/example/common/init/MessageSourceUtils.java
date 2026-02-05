package org.example.common.init;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageSourceUtils {

    static MessageSource messageSource;

    static Locale locale;

    public static MessageSource messageSource() {
        return messageSource;
    }

    public static Locale locale() {
        return locale;
    }

    public static String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, locale);
    }

    public static String getMessage(String key) {
        return getMessage(key, null);
    }
}
