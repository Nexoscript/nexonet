package com.nexoscript.nexonet.packet;

import com.nexoscript.nexonet.api.crypto.CryptoType;
import com.nexoscript.nexonet.api.crypto.KeySize;
import com.nexoscript.nexonet.api.packet.IPacketManager;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.logger.NexonetLogger;
import com.nexoscript.nexonet.packet.crypto.CryptoManager;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PacketManager implements IPacketManager {
    private final Map<String, Class<? extends Packet>> packetRegistry;
    private final NexonetLogger logger;
    private final CryptoManager cryptoManager;
    private boolean useEncryption = true;

    public PacketManager(NexonetLogger logger, String path, CryptoType type, KeySize size) {
        this.packetRegistry = new HashMap<>();
        this.logger = logger;
        this.cryptoManager = new CryptoManager(this.logger);
        this.cryptoManager.initCrypto(path, type, size);
    }

    public PacketManager(NexonetLogger logger) {
        this.packetRegistry = new HashMap<>();
        this.logger = logger;
        this.cryptoManager = new CryptoManager(this.logger);
        this.useEncryption = false;
    }

    public void registerPacketType(String type, Class<? extends Packet> clazz) {
        this.packetRegistry.put(type, clazz);
    }

    public String toJson(Packet packet) {
        JSONObject json = new JSONObject();
        json.put("type", packet.getType());
        Field[] fields = packet.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                json.put(field.getName(), field.get(packet));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(useEncryption) {
            System.out.println("[Crypto Decrypted] : " + json.toString());
            String s = this.cryptoManager.encryptString(json.toString());
            System.out.println("[Crypto Encrypted] : " + s);
            return s;
        }
        return json.toString();
    }

    public Packet fromJson(String jsonString) {
        try {
            String encrytedString = jsonString;
            System.out.println("[Crypto Encrypted] : " + encrytedString);
            if(useEncryption)
                encrytedString = this.cryptoManager.decryptString(jsonString);
            System.out.println("[Crypto Decrypted] : " + encrytedString);
            JSONObject json = new JSONObject(encrytedString);
            String type = json.getString("type");
            Class<? extends Packet> clazz = this.packetRegistry.get(type);
            if (clazz == null) {
                throw new IllegalArgumentException("Unknown packet type: " + type);
            }
            Packet packet = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (json.has(field.getName())) {
                    Object value = json.get(field.getName());
                    field.set(packet, value);
                }
            }
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
