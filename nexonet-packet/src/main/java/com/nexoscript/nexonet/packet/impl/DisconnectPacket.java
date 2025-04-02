package com.nexoscript.nexonet.packet.impl;

import com.nexoscript.nexonet.api.packet.Packet;

public class DisconnectPacket extends Packet {
    private int code;

    public DisconnectPacket() {
        super("DISCONNECT");
    }

    public DisconnectPacket(int code) {
        super("DISCONNECT");
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "DisconnectPacket{" +
                "code=" + code +
                '}';
    }
}
