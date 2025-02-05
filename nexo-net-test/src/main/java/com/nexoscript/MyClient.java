package com.nexoscript;

import com.nexoscript.nexonet.api.crypto.CryptoType;
import com.nexoscript.nexonet.api.crypto.KeySize;
import com.nexoscript.nexonet.client.Client;
import com.nexoscript.nexonet.packet.crypto.CryptoManager;
import com.nexoscript.packets.BytePacket;
import com.nexoscript.packets.MessagePacket;

import java.util.Scanner;

public class MyClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Client client = new Client(true);
        CryptoManager cryptoManager = new CryptoManager(client.getLogger());
        cryptoManager.initCrypto("secret.key", CryptoType.AES, KeySize.KEY_256);
        Thread console = new Thread(() -> {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] command = line.split(":");
                switch (command[0]) {
                    case "send" ->  {
                        MessagePacket messagePacket = new MessagePacket(cryptoManager.encryptString(command[1]));
                        System.out.println(messagePacket.getMessage());
                        client.send(messagePacket);
                        System.out.println("[System] -> Send Message to Server!");
                    }
                    default -> System.out.println("[System] -> Unknown Command!");
                }
            }
        });
        client.getPacketManager().registerPacketType("MESSAGE_PACKET", MessagePacket.class);
        client.getPacketManager().registerPacketType("BYTES_PACKET", BytePacket.class);
        client.onClientConnect(iClient -> {
            System.out.println("Client connected with ID: " + iClient.getID());
        });
        client.onClientDisconnect(iClient -> {
            System.out.println("Client disconnected with ID: " + iClient.getID());
        });
        client.onClientReceived((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " received!");
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket.getMessage());
                System.out.println(cryptoManager.decryptString(messagePacket.getMessage()));
            }
        });
        client.onClientSend((iClient, packet) -> {});
        console.start();
        client.connect("127.0.0.1", 1234);
    }
}
