package com.nexoscript.nexonet.lib.defpacket.auth;

import com.nexoscript.nexonet.api.packet.Packet;

public class AuthPacketResponse extends Packet {
    private boolean success;
    public AuthPacketResponse(String type, boolean success) {
        super(type);
        this.success = success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public String toString() {
        return "AuthPacketResponse { " +
                "type=" + getType() + " | " +
                "success=" + success +
                " }";
    }
}
