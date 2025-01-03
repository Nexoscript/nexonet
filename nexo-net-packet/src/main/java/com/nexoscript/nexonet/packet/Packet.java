package com.nexoscript.nexonet.packet;

public abstract class Packet<T> {
    private String type;
    private T data;

    public Packet(String type) {
        this.type = type;
        this.data = null;
    }

    public Packet(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}
