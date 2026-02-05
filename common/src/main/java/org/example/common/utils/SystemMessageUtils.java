package org.example.common.utils;

import org.example.common.bo.Message;
import org.example.common.bo.User;
import org.example.common.channel.PokerChannel;
import org.example.common.init.MessageSourceUtils;

public class SystemMessageUtils {

    public static Message stringMessage(String msg, PokerChannel pokerChannel) {
        return stringMessage(pokerChannel == null ? null : pokerChannel.getUser(), msg);
    }

    public static Message stringMessage(User user, String msg) {
        Message message = new Message();
        message.setMessage(msg);
        message.setUser(user);
        return message;
    }

    public static Message messageSource(String code, PokerChannel pokerChannel) {
        String msg = MessageSourceUtils.getMessage(code);
        return stringMessage(pokerChannel == null ? null : pokerChannel.getUser(), msg);
    }

    public static Message messageSource(String code) {
        return messageSource(code, null);
    }
}
