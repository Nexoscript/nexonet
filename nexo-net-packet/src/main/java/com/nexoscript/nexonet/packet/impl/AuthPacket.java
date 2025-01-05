package com.nexoscript.nexonet.packet.impl;

import com.nexoscript.nexonet.api.packet.Packet;

public class AuthPacket extends Packet {
    private String id;

    public AuthPacket() {
        super("AUTH");
    }

    public AuthPacket(String id) {
        super("AUTH");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AuthPacket{" +
                "id='" + id + '\'' +
                '}';
    }
}
