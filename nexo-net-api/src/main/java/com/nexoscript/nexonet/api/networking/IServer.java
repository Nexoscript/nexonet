package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.utils.BiConsumer;

import java.net.Inet4Address;
import java.util.function.Consumer;

public interface IServer {
    String getID();
    Thread getThread();
    boolean isRunning();
    int getPort();
    Inet4Address getIpAddress();
    void listen(int port);
    void onConnect(Consumer<IClient> onConnect);
    void onDisconnect(Consumer<IClient> onDisconnect);
    void onSend(BiConsumer<IClient, Packet> onSend);
    void onReceive(BiConsumer<IClient, Packet> onReceive);
    void sendToClient(String clientID, Packet packet);
    void sendToClients(Packet packet);
}
