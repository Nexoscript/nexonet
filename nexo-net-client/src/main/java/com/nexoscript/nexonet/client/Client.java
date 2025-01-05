package com.nexoscript.nexonet.client;

import com.nexoscript.nexonet.logger.LoggingType;
import com.nexoscript.nexonet.logger.NexonetLogger;
import com.nexoscript.nexonet.packet.Packet;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private boolean isAuth;
    private boolean logging;
    private NexonetLogger logger;
    private String id;

    public Client() {
        this.initialize(false);
    }

    public Client(boolean logging) {
        this.initialize(logging);
    }

    private void initialize(boolean logging) {
        this.logging = logging;
        this.logger = new NexonetLogger(this.logging);
        PacketManager.registerPacketType("DATA", DataPacket.class);
        PacketManager.registerPacketType("AUTH", AuthPacket.class);
        PacketManager.registerPacketType("AUTH_RESPONSE", AuthResponsePacket.class);
        PacketManager.registerPacketType("DISCONNECT", DisconnectPacket.class);
    }

    public void connect() {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;
        this.logger.log(LoggingType.INFO, "Connecting to server...");
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    send(writer, new DisconnectPacket(0));
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
            this.logger.log(LoggingType.INFO, "Connected to server");
            String userInput;
            while (true) {
                this.logger.log(LoggingType.INFO, "Input: ", false);
                userInput = consoleReader.readLine();
                if (userInput.startsWith("message")) {
                    if (!isAuth) {
                        this.logger.log(LoggingType.INFO, "You need to authenticate you with the 'auth' command first.");
                        continue;
                    }
                    this.logger.log(LoggingType.INFO, "Send data packet to server.");
                    send(writer, new DataPacket(userInput.split(":")[1]));
                }
                if (userInput.equals("auth")) {
                    this.logger.log(LoggingType.INFO, "Send auth packet to server.");
                    send(writer, new AuthPacket(UUID.randomUUID().toString()));
                }
                if (userInput.equalsIgnoreCase("exit")) {
                    this.logger.log(LoggingType.INFO, "Connection closed.");
                    send(writer, new DisconnectPacket(0));
                    break;
                }
                String serverResponse;
                if(reader.read() > 0) {
                    if ((serverResponse = reader.readLine()) != null) {
                        String modifiedString = "{" + serverResponse;
                        System.out.println(modifiedString);
                        Packet packet = PacketManager.fromJson(new JSONObject(modifiedString));
                        if (packet instanceof DataPacket dataPacket) {
                            System.out.println(dataPacket.getString());
                        }
                        if (packet instanceof AuthResponsePacket authResponsePacket) {
                            if (authResponsePacket.isSuccess()) {
                                this.id = authResponsePacket.getId();
                                this.isAuth = true;
                                continue;
                            }
                            send(writer, new AuthPacket(UUID.randomUUID().toString()));
                            this.logger.log(LoggingType.INFO, "Send auth packet to server.");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(PrintWriter writer, Packet packet) {
        writer.println(PacketManager.toJson(packet));
        writer.flush();
    }

    public NexonetLogger getLogger() {
        return logger;
    }
}
