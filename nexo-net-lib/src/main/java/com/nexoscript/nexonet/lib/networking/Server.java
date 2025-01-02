package com.nexoscript.nexonet.lib.networking;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.networking.IServer;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;
import com.nexoscript.nexonet.lib.NexoNetLib;
import com.nexoscript.nexonet.lib.defpacket.DataPacket;
import com.nexoscript.nexonet.lib.defpacket.auth.AuthPacket;
import com.nexoscript.nexonet.lib.defpacket.auth.AuthPacketResponse;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Server extends Thread implements IServer {
    private String id;
    private boolean isRunning = false;
    private int port;
    private Inet4Address address;
    private ServerSocket serverSocket;
    private Map<String, IClient> clients;
    private Consumer<IClient> onConnect = (iClient) -> {};
    private Consumer<IClient> onDisconnect = (iClient) -> {};
    private BiConsumer<IClient, Packet> onSend = (iClient, packet) -> {};
    private BiConsumer<IClient, Packet> onReceive = (iClient, packet) -> {};

    public Server(String id, Inet4Address address) {
        super(id);
        this.id = id;
        try {
            this.address = address;
            this.clients = new HashMap<>();
            this.serverSocket = new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                System.out.println(this.serverSocket.accept().toString());
                var tempClient = new Client(UUID.randomUUID().toString(), this.serverSocket.accept());
                this.clients.put(tempClient.getID(), tempClient);
                this.onConnect.accept(tempClient);
                if (tempClient.getInputStream() == null) {
                    System.out.println("NULL INPUT STREAM");
                    continue;
                } else {
                    this.clients.forEach((clientID, client) -> {
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                            String line;
                            if ((line = reader.readLine()) != null) {
                                System.out.println("[Server] -> " + line);
                                this.onReceive.accept(tempClient, new DataPacket("DATA", line));
                                if(line.contains("ping")) {
                                    writer.write("Response from Server: pong");
                                    writer.flush();
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendToClient(String clientID, Packet packet) {
        var client = this.clients.get(clientID);
        NexoNetLib.getInstance()
                .getPacketManager()
                .serialize(client.getOutputStream(), packet);
        this.onSend.accept(client, packet);
    }

    @Override
    public void sendToClients(Packet packet) {
        this.clients.forEach((clientID, client ) -> {
            NexoNetLib.getInstance()
                    .getPacketManager()
                    .serialize(client.getOutputStream(), packet);
            this.onSend.accept(client, packet);
        });
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public Thread getThread() {
        return this;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public Inet4Address getIpAddress() {
        return this.address;
    }

    @Override
    public void listen(int port) {
        try {
            this.port = port;
            this.serverSocket.bind(new InetSocketAddress(this.address, this.port));
            this.isRunning = true;
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnect(Consumer<IClient> onConnect) {
        this.onConnect = onConnect;
    }

    @Override
    public void onDisconnect(Consumer<IClient> onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    @Override
    public void onSend(BiConsumer<IClient, Packet> onSend) {
        this.onSend = onSend;
    }

    @Override
    public void onReceive(BiConsumer<IClient, Packet> onReceive) {
        this.onReceive = onReceive;
    }
}
