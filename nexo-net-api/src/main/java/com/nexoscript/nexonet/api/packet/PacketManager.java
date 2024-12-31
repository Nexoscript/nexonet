package com.nexoscript.nexonet.api.packet;

import java.io.InputStream;
import java.io.OutputStream;

public interface PacketManager {
    void serialize(OutputStream outputStream, Packet packet);
    <T> T deserialize(InputStream inputStream);
}
