package com.nexoscript.nexonet.api.packet;

import java.io.InputStream;
import java.io.OutputStream;

public interface IPacketManager {
    Packet serialize(OutputStream outputStream, Packet packet);
    <T extends Packet> T deserialize(InputStream inputStream, Class<T> tClass);
}
