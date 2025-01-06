package com.nexoscript.nexonet.server;

import com.nexoscript.nexonet.api.networking.IClientHandler;
import com.nexoscript.nexonet.api.networking.IServer;
import com.nexoscript.nexonet.logger.LoggingType;
import com.nexoscript.nexonet.api.packet.Packet;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable, IClientHandler {
    private final IServer server;
    private final Socket clientSocket;
    private String id;
    private boolean isAuth = false;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Socket getClientSocket() {
        return this.clientSocket;
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
    public IServer getServer() {
        return this.server;
    }

    @Override
    public void run() {
        try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                this.reader = new BufferedReader(new InputStreamReader(input));
                this.writer = new PrintWriter(output, true);
            String clientMessage;
            if(this.clientSocket.isConnected()) {
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println(clientMessage);
                    Packet packet = PacketManager.fromJson(new JSONObject(clientMessage));
                    if (packet instanceof DisconnectPacket disconnectPacket) {
                        this.server.getLogger().log(LoggingType.INFO, "Client disconnected. Code: " + disconnectPacket.getCode());
                        this.server.getClients().remove(this);
                        isAuth = false;
                        this.server.getClientDisconnectEvent().onClientDisconnect(this);
                        break;
                    }
                    if (!isAuth) {
                        if (packet instanceof AuthPacket authPacket) {
                            this.id = authPacket.getId();
                            this.isAuth = true;
                            this.server.getClients().add(this);
                            this.server.sendToClient(this.id, new AuthResponsePacket(true, this.id));
                            this.server.getClientConnectEvent().onClientConnect(this);
                            continue;
                        }
                        this.server.sendToClient(this.id, new AuthResponsePacket(false, this.id));
                        continue;
                    }
                    this.server.getServerReceivedEvent().onServerReceived(this, packet);
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

    @Override
    public BufferedReader getReader() {
        return this.reader;
    }

    @Override
    public PrintWriter getWriter() {
        return this.writer;
    }
}
