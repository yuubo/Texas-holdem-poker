package org.example.common.handle;

import io.netty.buffer.ByteBuf;

public class Delimiter {

    private final ByteBuf messageEndMark;

    private final int maxLength;

    public Delimiter(ByteBuf messageEndMark, int maxLength) {
        this.messageEndMark = messageEndMark;
        this.maxLength = maxLength;
    }

    public int maxLength() {
        return maxLength;
    }

    public ByteBuf messageEndMark() {
        return messageEndMark;
    }
}
