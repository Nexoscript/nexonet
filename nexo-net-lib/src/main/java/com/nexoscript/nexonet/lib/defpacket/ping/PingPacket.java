package com.nexoscript.nexonet.lib.defpacket.ping;

import com.nexoscript.nexonet.api.packet.Packet;

public class PingPacket extends Packet {
    private int ping;

    public PingPacket(String type, int ping) {
        super(type);
        this.ping = ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public int getPing() {
        return ping;
    }

    @Override
    public String toString() {
        return "PingPacket { " +
                "type=" + getType() + " | " +
                "ping=" + ping +
                " }";
    }
}
