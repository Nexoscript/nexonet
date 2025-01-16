# nexo-net

> [!CAUTION]
> **nexo-net** is a user-friendly and modern networking library currently in a very early stage of development.  
> It should only be used for testing and contributions at this time.

## About

nexo-net aims to provide developers with a simple way to implement networking operations in their applications.  
The library is written in Java and leverages modern programming paradigms to offer an intuitive API.

## Installation

To use nexo-net in your project, add it as a dependency in your `pom.xml`:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.nexoscript</groupId>
    <artifactId>nexo-net</artifactId>
    <version>VERSION</version>
</dependency>
```

Version: https://jitpack.io/#Nexoscript/nexo-net

Ensure you are using the latest version by adjusting the version number accordingly.

## Getting Started
Here are a few simple examples to help you get started with nexo-net.

## Creating a Test Server

```java
import com.nexoscript.nexonet.server.Server;
import com.nexoscript.packets.MessagePacket;

public class Testserver {

    public static void main(String[] args) {
        Server server = new Server(true, true);
        server.getPacketManager().registerPacketType("MESSAGE_PACKET", MessagePacket.class);
        server.onClientConnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onClientDisconnect(client -> {
            System.out.println("Client connected with ID: " + client.getId());
        });
        server.onServerReceived((client, packet) -> {
            System.out.println("Server received from client with ID: " + client.getId());
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
                if(messagePacket.getMessage().equalsIgnoreCase("ping")) {
                    server.sendToClient(client.getId(), new MessagePacket("pong"));
                }
            }
        });
        server.onServerSend((client, packet) -> {
            System.out.println("Server send from client with ID: " + client.getId());
            if(packet instanceof MessagePacket dataPacket) {
                System.out.println(dataPacket);
            }
        });
        server.start(1234);
    }
}
```
This example demonstrates how to create a simple server that listens for incoming connections on port 1234.

## Creating a Test Client

```java 
import com.nexoscript.nexonet.client.Client;
import com.nexoscript.packets.MessagePacket;

public class Testclient {

    public static void main(String[] args) {
        Client client = new Client(true, true);
        client.getPacketManager().registerPacketType("MESSAGE_PACKET", MessagePacket.class);
        client.onClientConnect(iClient -> {
            System.out.println("Client connected with ID: " + iClient.getID());
            client.send(new MessagePacket("ping"));
        });
        client.onClientDisconnect(iClient -> {
            System.out.println("Client disconnected with ID: " + iClient.getID());
        });
        client.onClientReceived((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " received!");
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
            }
        });
        client.onClientSend((iClient, packet) -> {
            System.out.println("Client with ID: " + iClient.getID() + " send!");
            if(packet instanceof MessagePacket messagePacket) {
                System.out.println(messagePacket);
            }
        });
        client.connect("127.0.0.1", 1234);
    }
}
```

This example demonstrates how to create a client that connects to the previously created server.

## Packet Class

```java
import com.nexoscript.nexonet.api.packet.Packet;

public class MessagePacket extends Packet {
    private String message;

    public MessagePacket() {
        super("MESSAGE_PACKET");
    }

    public MessagePacket(String message) {
        super("MESSAGE_PACKET");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessagePacket{" +
                "message='" + message + '\'' +
                '}';
    }
}
```
This is the Packet Class that is used in Client Server Example.
If you need your own Packet than you can create your own Class with an empty Constructor and your Variables

## Contributions

As nexo-net is in a very early stage of development, contributions are welcome.
Please fork the repository, make your changes, and submit a pull request.

## License

This project is licensed under the MIT License.
For more information, see the [LICENSE](LICENSE).

Note: nexo-net is in a very early stage of development. Use it only for testing and contributions at this time.


