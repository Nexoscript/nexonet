package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.packet.Packet;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler implements Runnable {
    private final Server server;
    private final Socket clientSocket;
    private String id;
    private boolean isAuth = false;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    public String getId() {
        return id;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                writer = new PrintWriter(output, true);
            String clientMessage;
            if(this.clientSocket.isConnected()) {
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println(clientMessage);
                    Packet packet = PacketManager.fromJson(new JSONObject(clientMessage));
                    if (packet instanceof DataPacket dataPacket) {
                        if (isAuth) {
                            System.out.println("Empfangen von Client: " + dataPacket.getString());
                            send(new DataPacket("Server: " + dataPacket.getString().toUpperCase()));
                            continue;
                        }
                        send(new DataPacket("First you must Authenticate you with command auth"));
                        continue;
                    }
                    if (packet instanceof DisconnectPacket disconnectPacket) {
                        System.out.println("Client hat die Verbindung beendet. Code: " + disconnectPacket.getCode());
                        this.server.getClients().remove(this);
                        isAuth = false;
                        break;
                    }
                    if (packet instanceof AuthPacket authPacket) {
                        this.id = authPacket.getId();
                        this.isAuth = true;
                        this.server.getClients().add(this);
                        send(new AuthResponsePacket(true, this.id));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void send(Packet packet) {
        writer.println(PacketManager.toJson(packet));
        writer.flush();
    }
}
