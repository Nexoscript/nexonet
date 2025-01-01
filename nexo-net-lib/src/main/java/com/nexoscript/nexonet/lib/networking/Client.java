package com.nexoscript.nexonet.lib.networking;

import com.nexoscript.nexonet.api.networking.IClient;
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
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread implements IClient {
    private String id;
    private boolean isRunning = false;
    private boolean auth = false;
    private Socket socket;
    private Inet4Address address;
    private int port;
    private Consumer<IClient> onConnect = (client) -> {};
    private Consumer<IClient> onDisconnect = (client) -> {};
    private BiConsumer<IClient, Packet> onSend = (client, packet) -> {};
    private BiConsumer<IClient, Packet> onReceive = (client, packet) -> {};
    private OutputStream outputStream;
    private InputStream inputStream;

    public Client(String id) {
        super(id);
        this.id = id;
        this.setName(this.id);
        this.socket = new Socket();
    }

    public Client(String id, Socket socket) {
        super(id);
        this.id = id;
        this.setName(this.id);
        this.socket = socket;
    }

    @Override
    public void run() {
            while (isRunning) {
                try {
                    if(this.socket.isConnected() && !this.socket.isClosed()) {
                        NexoNetLib.getInstance().getPacketManager().serialize(this.socket.getOutputStream(), new AuthPacket("AUTH", this.id));
                        System.out.println("Test READING!");
                        this.inputStream = this.socket.getInputStream();
                        this.outputStream = this.socket.getOutputStream();
                        if (this.inputStream.available() > 0) {
                            Packet packet = NexoNetLib.getInstance().getPacketManager().deserialize(this.inputStream, Packet.class);
                            if (packet instanceof AuthPacketResponse response) {
                                if (response.isSuccess()) {
                                    setAuth(true);
                                    this.onConnect.accept(this);
                                } else {
                                    NexoNetLib.getInstance().getPacketManager().serialize(this.socket.getOutputStream(), new AuthPacket("AUTH", this.id));
                                }
                            }
                            if (isAuth()) {
                                this.onReceive.accept(this, packet);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    @Override
    public void send(Packet packet) {
        if(auth)
            this.onSend.accept(this, NexoNetLib.getInstance().getPacketManager().serialize(this.outputStream, packet));
    }

    @Override
    public void disconnect() {
        try {
            if(this.socket.isConnected()) {
                this.socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void setID(String id) {
        this.id = id;
        this.setName(this.id);
    }

    @Override
    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    public boolean isAuth() {
        return this.auth;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public Thread getThread() {
        return this;
    }

    @Override
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
    public void connect(Inet4Address address, int port) {
        try {
            this.address = address;
            this.port = port;
            this.socket.connect(new InetSocketAddress(address, port));
            System.out.println("Test CONNECT!");
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
