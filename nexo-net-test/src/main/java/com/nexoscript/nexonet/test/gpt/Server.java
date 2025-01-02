package com.nexoscript.nexonet.test.gpt;

import com.nexoscript.nexonet.test.gpt.packet.AuthPacket;
import com.nexoscript.nexonet.test.gpt.packet.AuthResponsePacket;
import com.nexoscript.nexonet.test.gpt.packet.DataPacket;
import com.nexoscript.nexonet.test.gpt.packet.DisconnectPacket;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {
    private PacketHandler packetManager;
    private List<ClientHandler> clients;

    public Server() {
        this.packetManager = new PacketHandler();
        this.packetManager.registerType("AUTH", AuthPacket.class);
        this.packetManager.registerType("AUTH_RESPONSE", AuthResponsePacket.class);
        this.packetManager.registerType("DATA", DataPacket.class);
        this.packetManager.registerType("DISCONNECT", DisconnectPacket.class);
        final int PORT = 12345;
        clients = new ArrayList<>();
        System.out.println("Server startet...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warten auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                System.out.println("Client verbunden: " + clientSocket.getInetAddress());

                // Startet einen neuen Thread für den Client
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PacketHandler getPacketManager() {
        return packetManager;
    }

    public static void main(String[] args) {
        new Server();
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}

class ClientHandler implements Runnable {
    private Server server;
    private Socket clientSocket;
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
                    this.server.getClients().forEach(clientHandler -> {
                        boolean isFinished = false;
                        String generatedId = "";
                        while (!isFinished) {
                            if (!generatedId.isEmpty()) {
                                if (clientHandler.getId().equalsIgnoreCase(generatedId)) {
                                    generatedId = UUID.randomUUID().toString();
                                } else {
                                    isFinished = true;
                                    this.id = generatedId;
                                    this.server.getClients().add(this);
                                    send(writer, new AuthResponsePacket(true, this.id));
                                    break;
                                }
                            } else {
                                if (clientHandler.getId().equalsIgnoreCase(authPacket.getId())) {
                                    generatedId = UUID.randomUUID().toString();
                                } else {
                                    isFinished = true;
                                    this.id = authPacket.getId();
                                    this.server.getClients().add(this);
                                    send(writer, new AuthResponsePacket(true, this.id));
                                    break;
                                }
                            }
                        }
                    });
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
