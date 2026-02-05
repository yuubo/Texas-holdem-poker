package org.example.common.channel;

import io.netty.channel.Channel;
import org.example.common.bo.User;

public class PokerChannel {

    private Channel channel;

    private User user;

    public PokerChannel(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
