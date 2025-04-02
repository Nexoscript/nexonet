package com.nexoscript.nexonet.api.crypto;

public enum KeySize {
    KEY_128(128),
    KEY_192(192),
    KEY_256(256);

    private final int size;

    KeySize(int size) {
        this.size = size;
    }

    public int size() {
        return this.size;
    }
}
