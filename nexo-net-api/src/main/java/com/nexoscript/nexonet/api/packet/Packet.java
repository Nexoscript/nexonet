package com.nexoscript.nexonet.api.packet;

import java.io.Serializable;

public abstract class Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;

    public Packet(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type='" + type + '\'' +
                '}';
    }
}
