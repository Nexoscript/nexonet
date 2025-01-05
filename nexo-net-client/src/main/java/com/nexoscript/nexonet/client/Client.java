package com.nexoscript.nexonet.client;

import com.nexoscript.nexonet.packet.Packet;
import com.nexoscript.nexonet.packet.PacketManager;
import com.nexoscript.nexonet.packet.impl.AuthPacket;
import com.nexoscript.nexonet.packet.impl.AuthResponsePacket;
import com.nexoscript.nexonet.packet.impl.DataPacket;
import com.nexoscript.nexonet.packet.impl.DisconnectPacket;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.UUID;

public class Client {
    private boolean isAuth = false;
    private String id;

    public Client() {
        PacketManager.registerPacketType("DATA", DataPacket.class);
        PacketManager.registerPacketType("AUTH", AuthPacket.class);
        PacketManager.registerPacketType("AUTH_RESPONSE", AuthResponsePacket.class);
        PacketManager.registerPacketType("DISCONNECT", DisconnectPacket.class);
    }

    public void connect() {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;
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
                            } else {
                                send(writer, new AuthPacket(UUID.randomUUID().toString()));
                                System.out.println("Client: Send Auth Packet");
                            }
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

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}
