package com.nexoscript.nexonet.lib.networking;

import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.networking.IServer;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.api.utils.BiConsumer;
import com.nexoscript.nexonet.lib.NexoNetLib;
import com.nexoscript.nexonet.lib.defpacket.auth.AuthPacket;
import com.nexoscript.nexonet.lib.defpacket.auth.AuthPacketResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
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
                var tempClient = new Client("", this.serverSocket.accept());
                if (tempClient.getInputStream() == null) {
                    System.out.println("NULL INPUT STREAM");
                    continue;
                }
                if (tempClient.getInputStream().available() > 0) {
                    Packet packet = NexoNetLib.getInstance().getPacketManager().deserialize(tempClient.getInputStream(), Packet.class);
                    System.out.println("DESEARLIZE PACKET");
                    if(packet instanceof AuthPacket authPacket) {
                        System.out.println("PACKET Auth");
                        tempClient.setID(authPacket.getId());
                        tempClient.setAuth(true);
                        this.clients.put(tempClient.getID(), tempClient);
                        NexoNetLib.getInstance()
                                .getPacketManager()
                                .serialize(tempClient.getOutputStream(), new AuthPacketResponse("AUTH_RESPONSE", true));
                        this.onConnect.accept(tempClient);
                    }
                }
                this.clients.forEach((clientID, client ) -> {
                    try {
                        if(client.getInputStream().available() > 0) {
                            if(client.isAuth()) {
                                this.onReceive.accept(client, NexoNetLib.getInstance()
                                        .getPacketManager().deserialize(client.getInputStream(), Packet.class));
                            }
                        }
                        if (client.getSocket().isClosed()) {
                            client.getSocket().close();
                            this.clients.remove(clientID, client);
                            this.onDisconnect.accept(client);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
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
