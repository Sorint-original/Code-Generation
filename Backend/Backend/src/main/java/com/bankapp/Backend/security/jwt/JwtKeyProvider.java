package com.bankapp.Backend.security.jwt;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

@Component
public class JwtKeyProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtKeyProvider() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream resourceAsStream = getClass().getResourceAsStream("/keystore.p12");
            keyStore.load(resourceAsStream, "mykeystorepassword".toCharArray());

            Key key = keyStore.getKey("bankkey", "mykeystorepassword".toCharArray());
            this.privateKey = (PrivateKey) key;

            Certificate certificate = keyStore.getCertificate("bankkey");
            this.publicKey = certificate.getPublicKey();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load keypair from keystore", e);
        }
    }
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

}
