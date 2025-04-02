package com.nexoscript.nexonet.api.crypto;

public enum CryptoType {
    AES("AES"),
    DES("DES"),
    RSA("RSA");

    private final String key;

    CryptoType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
