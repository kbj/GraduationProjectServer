import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.*;
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
import java.util.Random;
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

            SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, encoded);
            byte[] encrypt = aes.encrypt(new FileInputStream(new File("e:/9ea683b7d0a20cf45025ddac75094b36acaf9934.jpg")));

            byte[] decrypt = aes.decrypt(encrypt);
            FileWriter fileWriter = FileWriter.create(new File("e:/back.jpg"));
            fileWriter.write(decrypt, 0, decrypt.length);
            System.out.println(encoded.length);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHASH() {
        int apHash = HashUtil.apHash("weikai");
        long weikai = HashUtil.tianlHash("weikai");
        System.out.println(apHash);
        System.out.println(weikai);

        String url = "\"http://192.168.157.1:8080/file/download/-1512003541\"";
        String urlLink = JSON.parseObject(url, String.class);
        System.out.println(urlLink);
    }

    @Test
    public void testAES1(){
        File file = new File("C:\\Users\\frida\\Desktop\\1520684534088");
        String base = "i1RimCUqDkoHMO6hNXsHLWSFOZd6J5jmeKadJk7cN1o=";
        byte[] key = Base64.decode(base);
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        FileReader fileReader = FileReader.create(file);
        byte[] decrypt = aes.decrypt(fileReader.readBytes());

        System.out.println(decrypt.length);
        FileWriter fileWriter = FileWriter.create(new File("e:/a.jpg"));
        fileWriter.write(decrypt, 0, decrypt.length);
        System.out.println(decrypt);
    }

    @Test
    public void testMD5() {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String digestHex = md5.digestHex("\\WxHyYK+!d4+TUbR");
        System.out.println(digestHex);
    }

    @Test
    public void testDelete() {
        double v = Math.random() * 10000;
        int floor = (int) Math.floor(v);
        System.out.println(floor);

        Random random = new Random(System.currentTimeMillis());
        int i = random.nextInt(10000);
        System.out.println(i);

        int randomNum = 12;
        StringBuffer buffer = new StringBuffer();
        for (int i1 = 0; i1 < (4 - String.valueOf(randomNum).length()); i1++) {
            buffer.append(0);
        }
        buffer.append(randomNum);
        System.out.println(buffer.toString());
    }
}
