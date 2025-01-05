package com.nexoscript.nexonet.api.events.server;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.networking.IClientHandler;
import com.nexoscript.nexonet.api.packet.Packet;

@FunctionalInterface
public interface ServerSendEvent {
    void onServerSend(IClientHandler client, Packet packet);
}
