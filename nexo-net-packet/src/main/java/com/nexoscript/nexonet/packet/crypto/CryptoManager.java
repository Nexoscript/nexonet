package com.nexoscript.nexonet.packet.crypto;

import com.nexoscript.nexonet.api.crypto.CryptoType;
import com.nexoscript.nexonet.api.crypto.ICryptoManager;
import com.nexoscript.nexonet.api.crypto.KeySize;
import com.nexoscript.nexonet.logger.NexonetLogger;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoManager implements ICryptoManager {
    private NexonetLogger logger;
    private SecretKey secretKey;
    private Cipher cipher;

    public CryptoManager(NexonetLogger logger) {
        this.logger = logger;
    }

    @Override
    public void initCrypto(String path, CryptoType type, KeySize size) {
        try {
            File secretFile = new File(path);
            if (!secretFile.exists()) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(type.key());
                keyGenerator.init(size.size());
                this.secretKey = keyGenerator.generateKey();
                Files.createFile(Path.of(secretFile.toURI()));
                Files.write(secretFile.toPath(), Base64.getEncoder().encodeToString(secretKey.getEncoded()).getBytes());
                this.cipher = Cipher.getInstance(type.key());
                return;
            }
            byte[] encodedKey = Files.readAllBytes(Paths.get(path));
            String keyString = new String(encodedKey);
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            this.secretKey = new SecretKeySpec(decodedKey, type.key());
            this.cipher = Cipher.getInstance(type.key());
        } catch (NoSuchAlgorithmException | IOException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encryptString(String data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decryptString(String data) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }
}
