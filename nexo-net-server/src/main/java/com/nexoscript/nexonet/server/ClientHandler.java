package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.packet.Packet;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientHandler implements Runnable {
    private final Server server;
    private final Socket clientSocket;
    private String id;
    private boolean isAuth = false;

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
        try (
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true)) {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println(clientMessage);
                Packet<?> packet = this.server.getPacketManager().deserialize(clientMessage);
                if (packet instanceof DataPacket dataPacket) {
                    if (isAuth) {
                        System.out.println("Empfangen von Client: " + dataPacket.getData());
                        writer.println("Server: " + dataPacket.getString().toUpperCase());
                        writer.flush();
                    }
                    writer.println("Server: First you must Authenticate you with command: auth");
                    writer.flush();
                }
                if (packet instanceof DisconnectPacket disconnectPacket) {
                    System.out.println("Client hat die Verbindung beendet. Code: " + disconnectPacket.getCode());
                    this.server.getClients().remove(this);
                    isAuth = false;
                    break;
                }
                if (packet instanceof AuthPacket authPacket) {
                    AtomicReference<String> generatedId = new AtomicReference<>(authPacket.getId());
                    while (true) {
                        if (this.server.getClients().stream()
                                .anyMatch(handler -> handler.getId().equalsIgnoreCase(generatedId.get()))) {
                            generatedId.set(UUID.randomUUID().toString());
                            continue;
                        }
                        this.id = generatedId.get();
                        this.server.getClients().add(this);
                        send(writer, new AuthResponsePacket(true, this.id));
                        break;
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

    void send(PrintWriter writer, Packet<?> packet) {
        writer.println(this.server.getPacketManager().serialize(packet));
        writer.flush();
    }
}
