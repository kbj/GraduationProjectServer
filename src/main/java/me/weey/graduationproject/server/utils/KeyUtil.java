package me.weey.graduationproject.server.utils;

import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 将字节数组转换成对应的PublicKey or PrivateKey类型
 * Created by weikai on 2018/01/17/0017.
 */
public class KeyUtil {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 生成ECDH方式的公私密钥对
     * @return 包含公私密钥的对象
     */
    public static KeyPair generateKey() throws Exception {
        String name = "secp256r1";
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", "BC");
        kpg.initialize(new ECGenParameterSpec(name));
        //生成密钥
        return kpg.generateKeyPair();
    }

    /**
     * 把byte数组还原为PublicKey对象
     * @param remoteBytes
     * @return
     */
    public static ECPublicKey toPublicKey(byte[] remoteBytes) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(remoteBytes);
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cryptography error: could not initialize ECDH keyfactory!" + e);
            return null;
        }

        ECPublicKey remotePublicKey;

        try {
            remotePublicKey = (ECPublicKey)kf.generatePublic(ks);
        } catch (InvalidKeySpecException e) {
            System.out.println("Received invalid key specification from client" + e);
            return null;
        } catch (ClassCastException e) {
            System.out.println("Received valid X.509 key from client but it was not EC Public Key material" + e);
            return null;
        }

        return remotePublicKey;
    }

    public static ECPrivateKey toPrivateKey(byte[] remoteBytes) {
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(remoteBytes);
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cryptography error: could not initialize ECDH keyfactory!" + e);
            return null;
        }

        ECPrivateKey remotePrivateKey;

        try {
            remotePrivateKey = (ECPrivateKey)kf.generatePrivate(ks);
        } catch (InvalidKeySpecException e) {
            System.out.println("Received invalid key specification from client" + e);
            return null;
        } catch (ClassCastException e) {
            System.out.println("Received valid X.509 key from client but it was not EC Public Key material" + e);
            return null;
        }

        return remotePrivateKey;
    }

    //合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
