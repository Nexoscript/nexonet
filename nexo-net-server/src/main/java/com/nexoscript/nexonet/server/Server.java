package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;

    public Server() {
        PacketManager.registerPacketType("DATA", DataPacket.class);
        PacketManager.registerPacketType("AUTH", AuthPacket.class);
        PacketManager.registerPacketType("AUTH_RESPONSE", AuthResponsePacket.class);
        PacketManager.registerPacketType("DISCONNECT", DisconnectPacket.class);
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

    public static void main(String[] args) {
        Server server = new Server();
        server.connect();
    }

    public List<ClientHandler> getClients() {
        return this.clients;
    }
}
