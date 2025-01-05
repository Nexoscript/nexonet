package com.nexoscript;

import com.nexoscript.nexonet.client.Client;
import com.nexoscript.nexonet.packet.impl.DataPacket;

public class Testclient {

    public static void main(String[] args) {
        Client client = new Client(true);
        client.onClientConnect(iClient -> {
            System.out.println("Client connected with ID: " + iClient.getID());
            client.send(new DataPacket("ping"));
        });
        client.onClientDisconnect(iClient -> {
            System.out.println("Client disconnected with ID: " + iClient.getID());
        });
        client.onClientReceived((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " received!");
            if(packet instanceof DataPacket dataPacket) {
                System.out.println(dataPacket);
            }
        });
        client.onClientSend((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " send!");
            if(packet instanceof DataPacket dataPacket) {
                System.out.println(dataPacket);
            }
        });
        client.connect("127.0.0.1", 1234);
    }
}