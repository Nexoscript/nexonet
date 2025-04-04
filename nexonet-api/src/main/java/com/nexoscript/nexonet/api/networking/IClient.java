package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.events.client.ClientConnectEvent;
import com.nexoscript.nexonet.api.events.client.ClientDisconnectEvent;
import com.nexoscript.nexonet.api.events.client.ClientReceivedEvent;
import com.nexoscript.nexonet.api.events.client.ClientSendEvent;
import com.nexoscript.nexonet.api.events.server.ServerReceivedEvent;
import com.nexoscript.nexonet.api.events.server.ServerSendEvent;
import com.nexoscript.nexonet.api.packet.IPacketManager;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;
import com.nexoscript.nexonet.logger.NexonetLogger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

public interface IClient {
    String getId();
    void setId(String id);
    boolean isRunning();
    Socket getSocket();
    void disconnect();
    int getPort();
    String getHostname();
    void connect(String address, int port);
    void send(Packet packet);
    boolean isAuth();
    void setAuth(boolean auth);
    void onClientConnect(ClientConnectEvent event);
    void onClientDisconnect(ClientDisconnectEvent event);
    void onClientReceived(ClientReceivedEvent event);
    void onClientSend(ClientSendEvent event);
    NexonetLogger getLogger();
    IPacketManager getPacketManager();
}
