package ru.kodos.almaz2.rpc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.xml.bind.DatatypeConverter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Андрей on 10.04.2014.
 */
public class PSSSigner {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public PSSSigner() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

            FileReader privateReader = new FileReader("private_key.pem");
            PemObject privateKeyPem = new PemReader(privateReader).readPemObject();
            // decode private key
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyPem.getContent());
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateSpec);

            FileReader publicReader = new FileReader("public_key.pem");
            PemObject publicKeyPem = new PemReader(publicReader).readPemObject();
            // decode public key
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyPem.getContent());
            publicKey = (RSAPublicKey) keyFactory.generatePublic(publicSpec);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public String sign(String solt) {
        StringBuilder sb = new StringBuilder();
        try {
            Signature signature = Signature.getInstance("SHA1withRSA/PSS", "BC");
            signature.initSign(privateKey);
            signature.update(solt.getBytes());
            sb.append(DatatypeConverter.printBase64Binary(signature.sign()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean verify(String sign, String solt) {
        boolean result = false;
        try {
            Signature signature = Signature.getInstance("SHA1withRSA/PSS", "BC");
            signature.initVerify(publicKey);
            signature.update(solt.getBytes());
            result = signature.verify(DatatypeConverter.parseBase64Binary(sign));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return result;
    }
}
