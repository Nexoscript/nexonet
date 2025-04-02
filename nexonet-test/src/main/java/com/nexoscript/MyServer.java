package com.nexoscript;

import com.nexoscript.nexonet.api.crypto.CryptoType;
import com.nexoscript.nexonet.api.crypto.KeySize;
import com.nexoscript.nexonet.packet.crypto.CryptoManager;
import com.nexoscript.nexonet.server.Server;
import com.nexoscript.packets.MessagePacket;

import java.util.Scanner;

public class MyServer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Server server = new Server(true);
        CryptoManager cryptoManager = new CryptoManager(server.getLogger());
        cryptoManager.initCrypto("secret.key", CryptoType.AES, KeySize.KEY_256);
        Thread console = new Thread(() -> {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] command = line.split(":");
                switch (command[0]) {
                    case "send" -> server.getClients().forEach(client -> {
                        if(command[1].equals(client.getId())) {
                            MessagePacket messagePacket = new MessagePacket(cryptoManager.encryptString(command[2]));
                            System.out.println(messagePacket.getMessage());
                            server.sendToClient(client.getId(), messagePacket);
                            System.out.println("[System] -> Send Message to Client!");
                        }
                    });
                    default -> System.out.println("[System] -> Unknown Command!");
                }
            }
        });
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
                System.out.println(messagePacket.getMessage());
                System.out.println(cryptoManager.decryptString(messagePacket.getMessage()));
            }
        });
        server.onServerSend((client, packet) -> {});
        console.start();
        server.start(1234);
    }
}
