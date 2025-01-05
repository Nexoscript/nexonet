package com.nexoscript;

import com.nexoscript.nexonet.server.Server;

public class Testserver {

    public static void main(String[] args) {
        Server server = new Server(true);
        server.start();
    }
}