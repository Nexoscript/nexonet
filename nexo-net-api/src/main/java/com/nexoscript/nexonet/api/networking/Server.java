package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;

import java.net.Inet4Address;
import java.util.function.Consumer;

public interface Server {
    int getPort();
    Inet4Address getIpAddress();
    void listen(int port);
    void onConnect(Consumer<Client> onConnect);
    void onDisconnect(Consumer<Client> onDisconnect);
    void onSend(BiConsumer<Client, Packet> onSend);
    void onReceive(BiConsumer<Client, Packet> onReceive);
}
