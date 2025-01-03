package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.packet.PacketHandler;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private final PacketHandler packetManager;
    private List<ClientHandler> clients;

    public Server() {
        this.packetManager = new PacketHandler();
        this.packetManager.registerType("AUTH", AuthPacket.class);
        this.packetManager.registerType("AUTH_RESPONSE", AuthResponsePacket.class);
        this.packetManager.registerType("DATA", DataPacket.class);
        this.packetManager.registerType("DISCONNECT", DisconnectPacket.class);
    }

    public void connect() {
        final int PORT = 12345;
        clients = new ArrayList<>();
        System.out.println("Server startet...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warten auf Verbindungen...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                System.out.println("Client verbunden: " + clientSocket.getInetAddress());
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PacketHandler getPacketManager() {
        return packetManager;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.connect();
    }

    public List<ClientHandler> getClients() {
        return this.clients;
    }
}
