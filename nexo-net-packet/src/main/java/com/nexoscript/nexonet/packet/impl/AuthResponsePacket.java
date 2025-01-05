package com.nexoscript.nexonet.packet.impl;

import com.nexoscript.nexonet.api.packet.Packet;

public class AuthResponsePacket extends Packet {
    private boolean isSuccess;
    private String id;

    public AuthResponsePacket() {
        super("AUTH_RESPONSE");
    }

    public AuthResponsePacket(boolean isSuccess, String id) {
        super("AUTH_RESPONSE");
        this.id = id;
        this.isSuccess = isSuccess;
    }

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "AuthResponsePacket{" +
                "isSuccess=" + isSuccess +
                ", id='" + id + '\'' +
                '}';
    }
}
