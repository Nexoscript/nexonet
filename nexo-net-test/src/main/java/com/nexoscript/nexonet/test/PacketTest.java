package com.nexoscript.nexonet.test;

import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.lib.NexoNetLib;
import com.nexoscript.nexonet.lib.defpacket.auth.AuthPacket;

import java.io.*;

public class PacketTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        NexoNetLib lib = new NexoNetLib();

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());

        AuthPacket authPacket = new AuthPacket("AUTH", "TestID");

        lib.getPacketManager().serialize(byteOutputStream, authPacket);

        AuthPacket receivedPacket = lib.getPacketManager().deserialize(byteInputStream, AuthPacket.class);
        System.out.println("Original Packet: " + authPacket);
        System.out.println("Received Packet: " + receivedPacket);
    }
}
