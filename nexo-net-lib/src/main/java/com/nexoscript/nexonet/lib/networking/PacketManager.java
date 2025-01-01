package com.nexoscript.nexonet.lib.networking;

import com.google.gson.Gson;
import com.nexoscript.nexonet.api.packet.IPacketManager;
import com.nexoscript.nexonet.api.packet.Packet;

import java.io.*;

public class PacketManager implements IPacketManager {
    private Gson gson;

    @Override
    public Packet serialize(OutputStream outputStream, Packet packet) {
        if (gson == null) {
            gson = new Gson();
        }
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(gson.toJson(packet));
        printWriter.close();
        return packet;
    }

    @Override
    public <T extends Packet> T deserialize(InputStream inputStream, Class<T> tClass) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            if ((line = bufferedReader.readLine()) != null) {
                bufferedReader.close();
                return gson.fromJson(line, tClass);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
