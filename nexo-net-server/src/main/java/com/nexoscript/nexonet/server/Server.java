package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.api.events.client.ClientConnectEvent;
import com.nexoscript.nexonet.api.events.client.ClientDisconnectEvent;
import com.nexoscript.nexonet.api.events.server.ServerClientConnectEvent;
import com.nexoscript.nexonet.api.events.server.ServerClientDisconnectEvent;
import com.nexoscript.nexonet.api.events.server.ServerReceivedEvent;
import com.nexoscript.nexonet.api.events.server.ServerSendEvent;
import com.nexoscript.nexonet.api.networking.IClientHandler;
import com.nexoscript.nexonet.api.networking.IServer;
import com.nexoscript.nexonet.api.packet.Packet;
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

public class Server implements IServer {
    private List<IClientHandler> clients;
    private boolean logging;
    private NexonetLogger logger;
    private boolean isRunning = false;
    private int port;
    private String hostname = "127.0.0.1";
    private InetSocketAddress ip;
    private ServerClientConnectEvent clientConnectEvent;
    private ServerClientDisconnectEvent clientDisconnectEvent;
    private ServerReceivedEvent serverReceivedEvent;
    private ServerSendEvent serverSendEvent;

    public Server() {
        this.initialize(false);
    }

    public Server(String hostname) {
        this.hostname = hostname;
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
        this.clientConnectEvent = (client) -> {};
        this.clientDisconnectEvent = (client) -> {};
        this.serverReceivedEvent = (client, packet) -> {};
        this.serverSendEvent = (client, packet) -> {};
    }

    @Override
    public void start(int port) {
        this.port = port;
        this.ip = new InetSocketAddress(this.hostname, this.port);
        isRunning = true;
        this.logger.log(LoggingType.INFO, "Starting Nexonet server...");
        try (ServerSocket serverSocket = new ServerSocket(this.ip.getPort())) {
            this.logger.log(LoggingType.INFO, "Waiting for client connections...");
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                this.logger.log(LoggingType.INFO, "Client connected: " + clientSocket.getInetAddress());
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NexonetLogger getLogger() {
        return logger;
    }

    @Override
    public List<IClientHandler> getClients() {
        return this.clients;
    }

    @Override
    public void sendToClient(String clientID, Packet packet) {
        this.getClients().forEach(client -> {
            if(client.getId().equalsIgnoreCase(clientID)) {
                client.getWriter().println(PacketManager.toJson(packet));
                client.getWriter().flush();
                this.serverSendEvent.onServerSend(client, packet);
            }
        });
    }

    @Override
    public void sendToClients(Packet packet) {
        this.getClients().forEach(client -> {
                client.getWriter().println(PacketManager.toJson(packet));
                client.getWriter().flush();
                this.serverSendEvent.onServerSend(client, packet);
        });
    }

    @Override
    public void onClientConnect(ServerClientConnectEvent event) {
        this.clientConnectEvent = event;
    }

    @Override
    public void onClientDisconnect(ServerClientDisconnectEvent event) {
        this.clientDisconnectEvent = event;
    }

    @Override
    public void onServerReceived(ServerReceivedEvent event) {
        this.serverReceivedEvent = event;
    }

    @Override
    public void onServerSend(ServerSendEvent event) {
        this.serverSendEvent = event;
    }

    @Override
    public ServerClientConnectEvent getClientConnectEvent() {
        return this.clientConnectEvent;
    }

    @Override
    public ServerClientDisconnectEvent getClientDisconnectEvent() {
        return this.clientDisconnectEvent;
    }

    @Override
    public ServerReceivedEvent getServerReceivedEvent() {
        return this.serverReceivedEvent;
    }

    @Override
    public ServerSendEvent getServerSendEvent() {
        return this.serverSendEvent;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public InetSocketAddress getIpAddress() {
        return this.ip;
    }

    @Override
    public void setIpAddress(InetSocketAddress ipAddress) {
        this.ip = ipAddress;
    }
}
