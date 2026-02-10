package org.example.common.utils;

import org.example.common.message.Message;
import org.example.common.message.User;
import org.example.common.init.MessageSourceUtils;

public class SystemMessageUtils {

    public static Message stringMessage(String msg) {
        return stringMessage(null, msg);
    }

    public static Message stringMessage(User user, String msg) {
        Message message = new Message();
        message.setMessage(msg);
        message.setUser(user);
        return message;
    }

    public static Message messageSource(User user, String code, Object[] args) {
        String msg = MessageSourceUtils.getMessage(code, args);
        return stringMessage(user, msg);
    }

    public static Message messageSource(String code) {
        return messageSource(null, code, null);
    }
}
