package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.function.Consumer;

public interface IClient {
    String getID();
    void setID(String id);
    Thread getThread();
    boolean isRunning();
    Socket getSocket();
    void disconnect();
    int getPort();
    Inet4Address getIpAddress();
    void connect(Inet4Address address, int port);
    void onConnect(Consumer<IClient> onConnect);
    void onDisconnect(Consumer<IClient> onDisconnect);
    void onSend(BiConsumer<IClient, Packet> onSend);
    void onReceive(BiConsumer<IClient, Packet> onReceive);
    void send(Packet packet);
    OutputStream getOutputStream();
    InputStream getInputStream();
    boolean isAuth();
    void setAuth(boolean auth);
}
