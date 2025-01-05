package com.nexoscript.nexonet.api.events.client;

import com.nexoscript.nexonet.api.networking.IClient;

@FunctionalInterface
public interface ClientConnectEvent {
    void onClientConnect(IClient client);
}
