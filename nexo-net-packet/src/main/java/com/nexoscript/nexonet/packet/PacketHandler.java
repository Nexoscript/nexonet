package com.nexoscript.nexonet.packet;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

public class PacketHandler {
    private final Gson gson;
    private final Map<String, Class<?>> typeRegistry = new HashMap<>();

    public PacketHandler() {
        this.gson = new GsonBuilder().create();
    }

    public void registerType(String type, Class<?> clazz) {
        typeRegistry.put(type, clazz);
    }

    public String serialize(Packet<?> packet) {
        return gson.toJson(packet);
    }

    public Packet<?> deserialize(String json) {
        String type = gson.fromJson(json, Packet.class).getType();
        Class<?> clazz = typeRegistry.get(type);

        if (clazz != null) {
            return (Packet<?>) gson.fromJson(json, clazz);
        }

        throw new RuntimeException("Unknown packet type: " + type);
    }
}
