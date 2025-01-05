package com.nexoscript.nexonet.api.packet;

import netscape.javascript.JSObject;
import org.json.JSONObject;

public interface IPacketManager {
    static void registerPacketType(String type, Class<? extends Packet> clazz) {}
    static JSObject toJson(Packet packet) {return null;}
    static Packet fromJson(JSONObject jsonObject) {return null;}
}
