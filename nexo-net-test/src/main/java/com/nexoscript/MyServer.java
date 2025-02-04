package com.nexoscript;

import com.nexoscript.nexonet.server.Server;
import com.nexoscript.packets.MessagePacket;

public class MyServer {

    public static void main(String[] args) {
        Server server = new Server(true);
        server.getPacketManager().registerPacketType("MESSAGE_PACKET", MessagePacket.class);
        server.onClientConnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onClientDisconnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onServerReceived((client, packet) -> {
            System.out.println("Server received from client with ID: " + client.getId());
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
                if(messagePacket.getMessage().equalsIgnoreCase("ping")) {
                    server.sendToClient(client.getId(), new MessagePacket("pong"));
                }
            }
        });
        server.onServerSend((client, packet) -> {
            System.out.println("Server send from client with ID: " + client.getId());
            if(packet instanceof MessagePacket dataPacket) {
                System.out.println(dataPacket);
            }
        });
        server.start(1234);
    }
}
