package org.example.common.warp;

import java.net.SocketAddress;

public class UdpMessage {

    private String message;

    private SocketAddress SocketAddress;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SocketAddress getSocketAddress() {
        return SocketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        SocketAddress = socketAddress;
    }

}
