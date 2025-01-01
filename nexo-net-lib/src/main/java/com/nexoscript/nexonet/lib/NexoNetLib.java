package com.nexoscript.nexonet.lib;

import com.nexoscript.nexonet.api.NexoNetApi;
import com.nexoscript.nexonet.api.packet.IPacketManager;
import com.nexoscript.nexonet.lib.networking.PacketManager;

public class NexoNetLib implements NexoNetApi {
    private static NexoNetLib instance;
    private IPacketManager packetManager;

    public NexoNetLib() {
        instance = this;
        this.packetManager = new PacketManager();
    }

    @Override
    public IPacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public void setPacketManager(IPacketManager packetManager) {
        this.packetManager = packetManager;
    }

    public static NexoNetLib getInstance() {
        return instance;
    }
}
