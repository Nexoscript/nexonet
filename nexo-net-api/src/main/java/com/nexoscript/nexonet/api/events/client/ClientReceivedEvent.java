package com.nexoscript.nexonet.api.events.client;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.packet.Packet;

@FunctionalInterface
public interface ClientReceivedEvent {
    void onClientReceived(IClient client, Packet packet);
}
