package com.nexoscript.nexonet.packet.impl;

import com.nexoscript.nexonet.packet.Packet;

public class DataPacket extends Packet {
    private String data;

    public DataPacket() {
        super("DATA");
    }

    public DataPacket(String data) {
        super("DATA");
        this.data = data;
    }

    public String getString() {
        return data;
    }

    public void setString(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "data='" + data + '\'' +
                '}';
    }
}
