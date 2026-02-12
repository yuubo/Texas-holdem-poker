package org.example.service.context;

import io.netty.channel.EventLoop;
import org.example.common.message.Operate;
import org.example.service.channel.PokerChannel;
import org.example.service.game.PokerRoom;

public interface PokerContext {

    void startGame(EventLoop eventLoop);

    void addPlayer(PokerChannel pokerChannel);

    void operateHandle(Operate operate, PokerChannel channel);

    PokerRoom getPokerRoom(EventLoop eventLoop);

    void disconnect(PokerChannel pokerChannel);

}
