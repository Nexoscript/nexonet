package com.nexoscript.nexonet.packet.impl;

import com.nexoscript.nexonet.packet.Packet;

public class AuthPacket extends Packet<AuthPacket> {
    private String id;

    public AuthPacket(String id) {
        super("AUTH");
        setData(this);
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
