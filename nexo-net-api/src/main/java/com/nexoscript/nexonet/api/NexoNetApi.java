package com.nexoscript.nexonet.api;

import com.nexoscript.nexonet.api.networking.Server;
import com.nexoscript.nexonet.api.packet.PacketManager;

public interface NexoNetApi {
    PacketManager getPacketManager();
    void setPacketManager(Server server);
}
