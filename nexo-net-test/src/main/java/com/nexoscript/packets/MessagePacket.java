package com.nexoscript.packets;

import com.nexoscript.nexonet.api.packet.Packet;

public class MessagePacket extends Packet {
    private String message;

    public MessagePacket() {
        super("MESSAGE_PACKET");
    }

    public MessagePacket(String message) {
        super("MESSAGE_PACKET");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessagePacket{" +
                "message='" + message + '\'' +
                '}';
    }
}