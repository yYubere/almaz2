import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Андрей on 29.03.2014.
 */
public class SecurityKeyTest {
    public static void main(String[] args) {
        try {
//            new SecurityKeyTest().go();
            new SecurityKeyTest().go2();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void go() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, InvalidKeyException, SignatureException {
        FileReader publicReader = new FileReader("public_key.pem");
        FileReader privateReader = new FileReader("private_key.pem");
        Security.addProvider(new BouncyCastleProvider());
        PemObject publicKeyPem = new PemReader(publicReader).readPemObject();
        PemObject privateKeyPem = new PemReader(privateReader).readPemObject();


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // decode public key
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicKeyPem.getContent());
        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

        // decode private key
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateKeyPem.getContent());
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privKey);
        String session = new String("11111");
        signature.update(session.getBytes());
        String sign = new String(signature.sign());
        System.out.println(sign);


    }

    private void go2() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, InvalidKeyException, SignatureException {
        FileReader privateReader = new FileReader("private_key.pem");
        Security.addProvider(new BouncyCastleProvider());
        PemObject privateKeyPem = new PemReader(privateReader).readPemObject();
        RSAKeyParameters keyParameters = (RSAKeyParameters) PrivateKeyFactory.createKey(privateKeyPem.getContent());

        RSAEngine rsaEngine = new RSAEngine();
        rsaEngine.init(true, keyParameters);
        PSSSigner signer = new PSSSigner(rsaEngine, new SHA1Digest(), 20);
    }
}
