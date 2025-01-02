package com.nexoscript.nexonet.test.gpt.packet;

import com.nexoscript.nexonet.test.gpt.Packet;

public class DisconnectPacket extends Packet<DisconnectPacket> {
    private int code;

    public DisconnectPacket(int code) {
        super("DISCONNECT");
        setData(this);
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
