package com.nexoscript.nexonet.lib.defpacket;

import com.nexoscript.nexonet.api.packet.Packet;

public class DataPacket extends Packet {
    private String data;

    public DataPacket(String type, String data) {
        super(type);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
