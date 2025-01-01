package com.nexoscript.nexonet.test;

import com.nexoscript.nexonet.lib.NexoNetLib;
import com.nexoscript.nexonet.lib.networking.Client;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String[] args) {
        try {
            NexoNetLib nexoNetLib = new NexoNetLib();
            Client client = new Client("TestClient");
            client.onConnect((iClient) -> {
                System.out.println("[TestClient] -> connected!");
            });
            client.onDisconnect((iClient) -> {
                System.out.println("[TestClient] -> disconnected!");
            });
            client.onSend((iClient, packet) -> {
                System.out.println("[TestClient] -> Client has Send! Packet");
            });
            client.onReceive((iClient, packet) -> {
                System.out.println("[TestClient] -> Client has Received! Packet");
            });
            client.connect((Inet4Address) Inet4Address.getByName("127.0.0.1"), 8080);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
