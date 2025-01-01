package com.nexoscript.nexonet.lib.defpacket.auth;

import com.nexoscript.nexonet.api.packet.Packet;

public class AuthPacket extends Packet {
    private String id;
    public AuthPacket(String type, String id) {
        super(type);
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AuthPacket { " +
                "type=" + getType() + " | " +
                "id=" + id +
                " }";
    }
}
