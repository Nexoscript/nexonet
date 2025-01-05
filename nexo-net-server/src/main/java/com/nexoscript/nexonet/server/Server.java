package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.logger.LoggingType;
import com.nexoscript.nexonet.logger.NexonetLogger;
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
    private boolean logging;
    private NexonetLogger logger;

    public Server() {
        this.initialize(false);
    }

    public Server(boolean logging) {
        this.initialize(logging);
    }

    private void initialize(boolean logging) {
        this.clients = new ArrayList<>();
        this.logging = logging;
        this.logger = new NexonetLogger(this.logging);
        PacketManager.registerPacketType("DATA", DataPacket.class);
        PacketManager.registerPacketType("AUTH", AuthPacket.class);
        PacketManager.registerPacketType("AUTH_RESPONSE", AuthResponsePacket.class);
        PacketManager.registerPacketType("DISCONNECT", DisconnectPacket.class);
    }

    public void start() {
        final int PORT = 12345;
        this.logger.log(LoggingType.INFO, "Starting Nexonet server...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            this.logger.log(LoggingType.INFO, "Waiting for client connections...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                this.logger.log(LoggingType.INFO, "Client connected: " + clientSocket.getInetAddress());
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NexonetLogger getLogger() {
        return logger;
    }

    public List<ClientHandler> getClients() {
        return this.clients;
    }
}
