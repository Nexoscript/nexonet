package com.nexoscript.nexonet.client;

import com.nexoscript.nexonet.packet.Packet;
import com.nexoscript.nexonet.packet.PacketHandler;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;

import java.io.*;
import java.net.*;
import java.util.UUID;

public class Client {
    private PacketHandler packetManager;
    private boolean isAuth = false;
    private String id;

    public Client() {
        this.packetManager = new PacketHandler();
        this.packetManager.registerType("AUTH", AuthPacket.class);
        this.packetManager.registerType("AUTH_RESPONSE", AuthResponsePacket.class);
        this.packetManager.registerType("DATA", DataPacket.class);
        this.packetManager.registerType("DISCONNECT", DisconnectPacket.class);
    }

    public void connect() {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> send(writer, new DisconnectPacket(0))));
            System.out.println("Verbunden mit dem Server.");
            String userInput;
            while (true) {
                System.out.print("Nachricht an Server: ");
                userInput = consoleReader.readLine();
                if (userInput.startsWith("message")) {
                    if (!isAuth) {
                        System.out.println("You need to authenticate you with the command 'auth'");
                        continue;
                    }
                    System.out.println("Client: Sende DatenPacket!");
                    send(writer, new DataPacket(userInput.split(":")[1]));
                }
                if (userInput.equals("auth")) {
                    System.out.println("Client: Send Auth Packet!");
                    send(writer, new AuthPacket(UUID.randomUUID().toString()));
                }
                if (userInput.equalsIgnoreCase("exit")) {
                    System.out.println("Verbindung beendet.");
                    send(writer, new DisconnectPacket(0));
                    break;
                }
                String serverResponse = reader.readLine();
                System.out.println(serverResponse);
                Packet<?> packet = packetManager.deserialize(serverResponse);
                if (packet instanceof DataPacket dataPacket) {
                    System.out.println(dataPacket.getString());
                }
                if (packet instanceof AuthResponsePacket authResponsePacket) {
                    if (authResponsePacket.isSuccess()) {
                        this.id = authResponsePacket.getId();
                        this.isAuth = true;
                    } else {
                        send(writer, new AuthPacket(UUID.randomUUID().toString()));
                        System.out.println("Client: Send Auth Packet");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PacketHandler getPacketManager() {
        return packetManager;
    }

    public void send(PrintWriter writer, Packet<?> packet) {
        System.out.println(1);
        writer.println(packetManager.serialize(packet));
        System.out.println(2);
        writer.flush();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}
