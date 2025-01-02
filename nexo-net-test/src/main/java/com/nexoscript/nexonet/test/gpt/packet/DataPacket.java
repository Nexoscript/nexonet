package com.nexoscript.nexonet.test.gpt.packet;

import com.nexoscript.nexonet.test.gpt.Packet;

public class DataPacket extends Packet<DataPacket> {
    private String data;

    public DataPacket(String data) {
        super("DATA");
        setData(this);
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
