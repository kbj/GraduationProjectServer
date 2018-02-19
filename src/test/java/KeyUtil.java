import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by weikai on 2018/01/17/0017.
 */
public class KeyUtil {

    /**
     * 把byte数组还原为PublicKey对象
     * @param remoteBytes
     * @return
     */
    public static ECPublicKey toPublicKey(byte[] remoteBytes) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(remoteBytes);
        KeyFactory kf;
        try {
            kf = java.security.KeyFactory.getInstance("ECDH");
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
            kf = java.security.KeyFactory.getInstance("ECDH");
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
}
