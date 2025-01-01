package com.nexoscript.nexonet.test;

import com.nexoscript.nexonet.lib.NexoNetLib;
import com.nexoscript.nexonet.lib.networking.Server;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class TestServer {
    public static void main(String[] args) {
        try {
            NexoNetLib nexoNetLib = new NexoNetLib();
            Server server = new Server("TestServer", (Inet4Address) Inet4Address.getByName("127.0.0.1"));
            server.onConnect((iClient) -> {
                System.out.println("[TestServer] -> Client with ID " + iClient.getID() + " is connected!");
            });
            server.onDisconnect((iClient) -> {
                System.out.println("[TestServer] -> Client with ID " + iClient.getID() + " is disconnected!");
            });
            server.onSend((iClient, packet) -> {
                System.out.println("[TestServer] -> Server has Send! Packet");
            });
            server.onReceive((iClient, packet) -> {
                System.out.println("[TestServer] -> Server has Received! Packet");
            });
            server.listen(8080);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
