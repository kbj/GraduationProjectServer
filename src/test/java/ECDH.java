import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

import javax.crypto.Cipher;

public class ECDH {
    public static void main(String[] args) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        String name = "secp256r1";

        // NOTE just "EC" also seems to work here
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", "BC");
        kpg.initialize(new ECGenParameterSpec(name));

        // Key pair to store public and private key
        KeyPair keyPair = kpg.generateKeyPair();

        //取得私钥和公钥
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        ECPublicKey ecPublicKey = KeyUtil.toPublicKey(publicKey.getEncoded());
        ECPrivateKey ecPrivateKey = KeyUtil.toPrivateKey(privateKey.getEncoded());


        Cipher iesCipher = Cipher.getInstance("ECIES", "BC");
        iesCipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);


        //解密
        Cipher jie = Cipher.getInstance("ECIES", "BC");
        jie.init(Cipher.DECRYPT_MODE, ecPrivateKey);

        String message = "weikai";
        //加密后的字符
        byte[] bytes = iesCipher.doFinal(message.getBytes());
        System.out.print("加密后的字符：");
        for (Byte b : bytes) {
            System.out.print(b);
        }

        byte[] jiemi = jie.doFinal(bytes);
        String result = new String(jiemi, "UTF-8");
        System.out.println();
        System.out.println(result);
    }
}