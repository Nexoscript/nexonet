package com.nexoscript.packets;

import com.nexoscript.nexonet.api.packet.Packet;

import java.util.Arrays;

public class BytePacket extends Packet {
    private byte[] bytes;

    public BytePacket() {super("BYTES_PACKET");}

    public BytePacket(byte[] bytes) {
        super("BYTES_PACKET");
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "BytePacket{" +
                "bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
