package org.example.service.channel;

import io.netty.channel.Channel;
import org.example.common.message.User;

public class PokerChannel {

    private Channel channel;

    private User user;

    /**
     * {@link org.example.service.channel.PokerChannelStatusEnum}
     */
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
