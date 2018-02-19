import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.weey.graduationproject.server.entity.DataStructure;
import me.weey.graduationproject.server.utils.KeyUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.junit.Test;
import org.springframework.web.socket.WebSocketSession;
import sun.security.pkcs.PKCS7;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TestPublic {

    @Test
    public void getPrivateKey() throws Exception {

       /* File f = new File("E:\\privatekey.key");
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("ECDH");
        PrivateKey privateKey = kf.generatePrivate(spec);*/
    }

    @Test
    public void testUUID() {
        String randomUUID = RandomUtil.randomUUID();
        System.out.println(randomUUID);
        String simpleUUID = RandomUtil.simpleUUID();
        System.out.println(simpleUUID);
    }

    @Test
    public void testRegex() {
        boolean match = ReUtil.isMatch("[\\w-.]+@[\\w-]+(.[\\w_-]+)+", "345@mail.some_domain_name.com.uk");
        System.out.println(match);
    }

    @Test
    public void testSHA512() {
        Digester sha512 = new Digester(DigestAlgorithm.SHA512);
        String weikai = sha512.digestHex("weikai");
        System.out.println(weikai);
        System.out.println(weikai.length());
        String s = RandomUtil.simpleUUID();
        System.out.println(s);
    }

    @Test
    public void testAES256() throws UnsupportedEncodingException {
        byte[] aes_part1 = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        byte[] aes_part2 = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        byte[] merger = KeyUtil.byteMerger(aes_part1, aes_part2);
        System.out.println(aes_part1.length + aes_part2.length);
        System.out.println(merger.length);

        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, merger);
        //加密为16进制表示
        String encryptHex = aes.encryptHex("魏楷");
        System.out.println("加密内容：" + encryptHex);
        //解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);

        System.out.println(decryptStr);
    }

    @Test
    public void testJSONByte() {
/*        cn.hutool.core.io.file.FileReader fileReader = new FileReader("E:\\files\\code\\java\\GraduationProjectServer\\src\\main\\resources\\static\\profile.png");
        byte[] bytes = fileReader.readBytes();
        System.out.println(bytes[0] + bytes[5]);
        String s = JSON.toJSONString(bytes);

        JSONObject parse = JSON.parseObject(s);
        byte[] bytes1 = parse.getBytes("");
        System.out.println(bytes1[0] + bytes1[5]);*/
    }

    @Test
    public void tetsDic() throws FileNotFoundException {
        /*FileReader fileReader = new FileReader("E:\\BAT_Check_DomainName\\dict\\西部数码大字典\\拼音\\double.txt");
        List<String> strings = fileReader.readLines();
        FileWriter fileWriter = new FileWriter("e:/double.txt");
        for (String s : strings) {
            fileWriter.append(s.trim());
            fileWriter.append("\n");
        }*/
    }

    @Test
    public void testMap() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("111", "234");
        map.replace("111", "sdfsdf");
        System.out.println(map.get("111"));
    }

    @Test
    public void testAES() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(256, new SecureRandom((System.currentTimeMillis()+"").getBytes("UTF-8")));
            SecretKey secretKey = kgen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            System.out.println(encoded.length);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
