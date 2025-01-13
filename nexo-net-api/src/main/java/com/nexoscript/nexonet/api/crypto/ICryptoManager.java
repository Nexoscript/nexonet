package com.nexoscript.nexonet.api.crypto;

public interface ICryptoManager {
    void initCrypto(String path, CryptoType type, KeySize size);
    String encryptString(String data);
    String decryptString(String data);
}
