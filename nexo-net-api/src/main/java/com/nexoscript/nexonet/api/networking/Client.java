package com.nexoscript.nexonet.api.networking;

import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;

import java.net.Inet4Address;
import java.util.function.Consumer;

public interface Client {
    int getPort();
    Inet4Address getIpAddress();
    void connect(Inet4Address address, int port);
    void onConnect(Consumer<Client> onConnect);
    void onDisconnect(Consumer<Client> onDisconnect);
    void onSend(BiConsumer<Client, Packet> onSend);
    void onReceive(BiConsumer<Client, Packet> onReceive);
}
