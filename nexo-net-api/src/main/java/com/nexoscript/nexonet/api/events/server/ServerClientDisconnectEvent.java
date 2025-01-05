package com.nexoscript.nexonet.api.events.server;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.networking.IClientHandler;

@FunctionalInterface
public interface ServerClientDisconnectEvent {
    void onClientDisconnect(IClientHandler client);
}
