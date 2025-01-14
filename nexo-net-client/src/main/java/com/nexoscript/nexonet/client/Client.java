package com.nexoscript.nexonet.client;

import com.nexoscript.nexonet.api.crypto.CryptoType;
import com.nexoscript.nexonet.api.crypto.KeySize;
import com.nexoscript.nexonet.api.events.client.ClientConnectEvent;
import com.nexoscript.nexonet.api.events.client.ClientDisconnectEvent;
import com.nexoscript.nexonet.api.events.client.ClientReceivedEvent;
import com.nexoscript.nexonet.api.events.client.ClientSendEvent;
import com.nexoscript.nexonet.api.networking.IClient;
import com.nexoscript.nexonet.api.packet.IPacketManager;
import com.nexoscript.nexonet.logger.LoggingType;
import com.nexoscript.nexonet.logger.NexonetLogger;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class Client implements IClient {
    private boolean isAuth;
    private boolean logging;
    private NexonetLogger logger;
    private String id;
    private boolean isRunning = false;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String hostname;
    private int port;
    private ClientConnectEvent clientConnectEvent;
    private ClientDisconnectEvent clientDisconnectEvent;
    private ClientReceivedEvent clientReceivedEvent;
    private ClientSendEvent clientSendEvent;
    private IPacketManager packetManager;

    public Client(boolean useCrypto) {
        this.logging = false;
        this.logger = new NexonetLogger(false);
        if(!useCrypto) {
            this.packetManager = new PacketManager(this.logger, "secret.key", CryptoType.RSA, KeySize.KEY_128);
        } else {
            this.packetManager = new PacketManager(this.logger);
        }
        this.initialize();
    }

    public Client(boolean logging, boolean useCrypto) {
        this.logging = logging;
        this.logger = new NexonetLogger(logging);
        if(!useCrypto) {
            this.packetManager = new PacketManager(this.logger, "secret.key", CryptoType.RSA, KeySize.KEY_128);
        } else {
            this.packetManager = new PacketManager(this.logger);
        }
        this.initialize();
    }

    public Client(boolean logging, boolean useCrypto, String path, CryptoType type, KeySize size) {
        this.logging = false;
        this.logger = new NexonetLogger(false);
        if(!useCrypto) {
            this.packetManager = new PacketManager(this.logger, path, type, size);
        } else {
            this.packetManager = new PacketManager(this.logger);
        }
        this.initialize();
    }

    private void initialize() {
        this.packetManager.registerPacketType("AUTH", AuthPacket.class);
        this.packetManager.registerPacketType("AUTH_RESPONSE", AuthResponsePacket.class);
        this.packetManager.registerPacketType("DISCONNECT", DisconnectPacket.class);
        this.clientConnectEvent = (client) -> {};
        this.clientDisconnectEvent = (client) -> {};
        this.clientReceivedEvent = (client, packet) -> {};
        this.clientSendEvent = (client, packet) -> {};
    }

    @Override
    public void connect(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.logger.log(LoggingType.INFO, "Connecting to server...");
        try  {
            this.socket = new Socket(this.hostname, this.port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            Runtime.getRuntime().addShutdownHook(new Thread(this::disconnect));
            this.logger.log(LoggingType.INFO, "Connected to server");
            this.isRunning = true;
            while (isRunning) {
                if(!isAuth) {
                    this.logger.log(LoggingType.INFO, "Send auth packet to server.");
                    send(new AuthPacket(UUID.randomUUID().toString()));
                }
                String serverResponse;
                if(reader.read() > 0) {
                    if ((serverResponse = reader.readLine()) != null) {
                        String modifiedString = "{" + serverResponse;
                        System.out.println(modifiedString);
                        Packet packet = this.packetManager.fromJson(modifiedString);
                        if(this.isAuth) {
                            this.clientReceivedEvent.onClientReceived(this, packet);
                        }
                        if (packet instanceof AuthResponsePacket authResponsePacket) {
                            if (authResponsePacket.isSuccess()) {
                                this.id = authResponsePacket.getId();
                                this.isAuth = true;
                                this.clientConnectEvent.onClientConnect(this);
                                continue;
                            }
                            send(new AuthPacket(UUID.randomUUID().toString()));
                            this.logger.log(LoggingType.INFO, "Send auth packet to server.");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(Packet packet) {
        writer.println(this.packetManager.toJson(packet));
        writer.flush();
        this.clientSendEvent.onClientSend(this, packet);
    }

    @Override
    public NexonetLogger getLogger() {
        return logger;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void disconnect() {
        try {
            this.logger.log(LoggingType.INFO, "Client Try to Disconnect!");
            send(new DisconnectPacket(0));
            this.clientDisconnectEvent.onClientDisconnect(this);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public boolean isAuth() {
        return this.isAuth;
    }

    @Override
    public void setAuth(boolean auth) {
        this.isAuth = auth;
    }

    @Override
    public void onClientConnect(ClientConnectEvent event) {
        this.clientConnectEvent = event;
    }

    @Override
    public void onClientDisconnect(ClientDisconnectEvent event) {
        this.clientDisconnectEvent = event;
    }

    @Override
    public void onClientReceived(ClientReceivedEvent event) {
        this.clientReceivedEvent = event;
    }

    @Override
    public void onClientSend(ClientSendEvent event) {
        this.clientSendEvent = event;
    }
}
