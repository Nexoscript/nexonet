package com.nexoscript.nexonet.test.gpt;

import com.nexoscript.nexonet.test.gpt.packet.AuthPacket;
import com.nexoscript.nexonet.test.gpt.packet.AuthResponsePacket;
import com.nexoscript.nexonet.test.gpt.packet.DataPacket;
import com.nexoscript.nexonet.test.gpt.packet.DisconnectPacket;

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
                    System.out.println("Client: Sende DatenPacket!");
                    send(writer, new DataPacket(userInput.split(":")[1]));
                }

                if (userInput.equals("auth")) {
                    System.out.println("Client: Sende Auth Packet!");
                    send(writer, new AuthPacket(UUID.randomUUID().toString()));
                }
                // Verbindung beenden, wenn "exit" eingegeben wird
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
        writer.println(packetManager.serialize(packet));
        writer.flush();
    }

    public static void main(String[] args) {
        new Client();
    }
}
