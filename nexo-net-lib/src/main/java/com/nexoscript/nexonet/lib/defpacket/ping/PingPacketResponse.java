package com.nexoscript.nexonet.lib.defpacket.ping;

import com.nexoscript.nexonet.api.packet.Packet;

public class PingPacketResponse extends Packet {
    private boolean success;

    public PingPacketResponse(String type, boolean success) {
        super(type);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "PingPacketResponse { " +
                "type=" + getType() + " | " +
                "success=" + success +
                " }";
    }
}
