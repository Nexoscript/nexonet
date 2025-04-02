package com.nexoscript.nexonet.api.packet;

public abstract class Packet {
    private String type;

    public Packet() {}

    public Packet(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}