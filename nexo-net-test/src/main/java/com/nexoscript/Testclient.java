package com.nexoscript;

import com.nexoscript.nexonet.client.Client;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.packets.MessagePacket;

public class Testclient {

    public static void main(String[] args) {
        PacketManager.registerPacketType("MESSAGE_PACKET", MessagePacket.class);
        Client client = new Client(false);
        client.onClientConnect(iClient -> {
            System.out.println("Client connected with ID: " + iClient.getID());
            client.send(new MessagePacket("ping"));
        });
        client.onClientDisconnect(iClient -> {
            System.out.println("Client disconnected with ID: " + iClient.getID());
        });
        client.onClientReceived((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " received!");
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
            }
        });
        client.onClientSend((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " send!");
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
            }
        });
        client.connect("127.0.0.1", 1234);
    }
}