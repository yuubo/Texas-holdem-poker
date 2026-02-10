package org.example.service.game;

import io.netty.channel.EventLoop;
import jakarta.annotation.PostConstruct;
import org.example.common.message.Operate;
import org.example.service.channel.PokerChannel;
import org.example.service.channel.PokerChannelStatusEnum;
import org.example.service.context.ApplicationContextGatherUtils;
import org.example.service.context.PokerContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ThPokerContext implements PokerContext {

    private Map<EventLoop, PokerRoom> pokerRoomMap = new HashMap<>();

    private AtomicInteger roomId = new AtomicInteger(1);

    @PostConstruct
    public void init() {
        ApplicationContextGatherUtils.pokerContext = this;
    }

    private PokerRoom createPokerRoom(EventLoop eventLoop) {
        PokerRoom pokerRoom = pokerRoomMap.get(eventLoop);
        if (pokerRoom == null) {
            pokerRoom = new PokerRoom(eventLoop, roomId.getAndIncrement());
            pokerRoomMap.put(eventLoop, pokerRoom);
        }
        return pokerRoom;
    }

    @Override
    public void startGame(EventLoop eventLoop) {
        PokerRoom pokerRoom = pokerRoomMap.get(eventLoop);
        if (pokerRoom != null) {
            pokerRoom.start();
        }
    }

    @Override
    public void addPlayer(PokerChannel pokerChannel) {
        PokerRoom pokerRoom = pokerRoomMap.get(pokerChannel.getChannel().eventLoop());
        if (pokerRoom == null) {
            pokerRoom = createPokerRoom(pokerChannel.getChannel().eventLoop());
        }
        pokerRoom.addPlayer(pokerChannel);
    }

    @Override
    public void operateHandle(Operate operate, PokerChannel channel) {
        PokerRoom pokerRoom = pokerRoomMap.get(channel.getChannel().eventLoop());
        if (pokerRoom != null) {
            pokerRoom.operateHandle(operate, channel);
        }
    }

    @Override
    public PokerRoom getPokerRoom(EventLoop eventLoop) {
        return pokerRoomMap.get(eventLoop);
    }

    @Override
    public void disconnect(PokerChannel pokerChannel) {
        pokerRoomMap.get(pokerChannel.getChannel().eventLoop()).disconnect(pokerChannel);
    }

}
