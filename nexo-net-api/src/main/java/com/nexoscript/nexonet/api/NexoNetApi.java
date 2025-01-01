package com.nexoscript.nexonet.api;

import com.nexoscript.nexonet.api.packet.IPacketManager;

public interface NexoNetApi {
    IPacketManager getPacketManager();
    void setPacketManager(IPacketManager packetManager);
}
