package com.nexoscript;

import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.server.Server;

public class Testserver {

    public static void main(String[] args) {
        Server server = new Server(true);
        server.onClientConnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onClientDisconnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onServerReceived((client, packet) -> {
            System.out.println("Server received from client with ID: " + client.getId());
            if(packet instanceof DataPacket dataPacket) {
                System.out.println(dataPacket);
                if(dataPacket.getString().equalsIgnoreCase("ping")) {
                    server.sendToClient(client.getId(), new DataPacket("pong"));
                }
            }
        });
        server.onServerSend((client, packet) -> {
            System.out.println("Server send from client with ID: " + client.getId());
            if(packet instanceof DataPacket dataPacket) {
                System.out.println(dataPacket);
            }
        });
        server.start(1234);
    }
}