package com.nexoscript.nexonet.api.packet;

public interface IPacketManager {
    void registerPacketType(String type, Class<? extends Packet> clazz);
    String toJson(Packet packet);
    Packet fromJson(String jsonObject);
}
