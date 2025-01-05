package com.nexoscript;

import com.nexoscript.nexonet.client.Client;

public class Testclient {

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}