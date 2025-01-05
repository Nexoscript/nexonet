package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.events.server.ServerClientConnectEvent;
import com.nexoscript.nexonet.api.events.server.ServerClientDisconnectEvent;
import com.nexoscript.nexonet.api.events.server.ServerReceivedEvent;
import com.nexoscript.nexonet.api.events.server.ServerSendEvent;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.logger.NexonetLogger;

import java.net.InetSocketAddress;
import java.util.List;

public interface IServer {
    boolean isRunning();
    int getPort();
    void setPort(int port);
    InetSocketAddress getIpAddress();
    void setIpAddress(InetSocketAddress ipAddress);
    void start(int port);
    NexonetLogger getLogger();
    List<IClientHandler> getClients();
    void sendToClient(String clientID, Packet packet);
    void sendToClients(Packet packet);
    void onClientConnect(ServerClientConnectEvent event);
    void onClientDisconnect(ServerClientDisconnectEvent event);
    void onServerReceived(ServerReceivedEvent event);
    void onServerSend(ServerSendEvent event);
    ServerClientConnectEvent getClientConnectEvent();
    ServerClientDisconnectEvent getClientDisconnectEvent();
    ServerReceivedEvent getServerReceivedEvent();
    ServerSendEvent getServerSendEvent();
}
