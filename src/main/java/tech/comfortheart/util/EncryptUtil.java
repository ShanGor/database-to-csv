package tech.comfortheart.util;


import sun.security.pkcs12.PKCS12KeyStore;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Use public key to encrypt the password, and private key to decrypt.
 */
public class EncryptUtil {
    public static final String PADDING = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    public static final String STORE_TYPE = "PKCS12";

    private static final PublicKey readPublicKey(final String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream(filename));
        return cert.getPublicKey();
    }

    private static final PrivateKey readPrivateKey(final String filename, final String storePassword, final String keyPassword, final String alias) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, UnrecoverableKeyException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(Files.newInputStream(Paths.get(filename), StandardOpenOption.READ), storePassword.toCharArray());
        Key key = keyStore.getKey(alias, keyPassword.toCharArray());
        return (PrivateKey) key;
    }

    /**
     * Encrypt with public key in X.509 format, final as base64 String.
     * @param certPath
     * @param password
     * @return
     * @throws Exception
     */
    public static final String encrypt(final String certPath, final String password) throws Exception {
        PublicKey key = readPublicKey(certPath);

        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));

    }

    /**
     * Decrypt the base64 string with private key in PKCS12 keystore.
     * @param keystorePath
     * @param alias
     * @param storePassword
     * @param keyPassword
     * @param encryptedBase64Str
     * @return
     * @throws Exception
     */
    public static final String decrypt(final String keystorePath,
                                       final String alias,
                                       final String storePassword,
                                       final String keyPassword,
                                       final String encryptedBase64Str) throws Exception {
        PrivateKey key = readPrivateKey(keystorePath, storePassword, keyPassword, alias);
        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedBase64Str)));
    }
}
