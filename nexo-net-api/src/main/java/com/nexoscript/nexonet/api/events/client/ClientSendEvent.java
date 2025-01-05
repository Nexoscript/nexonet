package com.nexoscript.nexonet.api.events.client;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.packet.Packet;

@FunctionalInterface
public interface ClientSendEvent {
    void onClientSend(IClient client, Packet packet);
}
