import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.utils.*;
import me.weey.graduationproject.server.utils.KeyUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class TestTime {
    @Test
    public void testZone() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sdf.setTimeZone(TimeZone.getTimeZone("GMT+10"));
        String snow = sdf.format(date);  // 2009-11-19 14:12:23
        System.out.println(snow);
    }

    @Test
    public void testDate(){
        try {
            KeyPair keyPair = me.weey.graduationproject.server.utils.KeyUtil.generateKey();
            PublicKey aPublic = keyPair.getPublic();
            System.out.println(HexBin.encode(aPublic.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testECDHUtil() throws Exception {
        KeyPair keyPair = KeyUtil.generateKey();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();

        String publicKey = Base64.getEncoder().encodeToString(aPublic.getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(aPrivate.getEncoded());
        System.out.println(publicKey);
        System.out.println(privateKey);

        String enMsg = ECDHUtil.encryption("魏楷".getBytes("UTF-8"), Base64.getEncoder().encodeToString(aPublic.getEncoded()), null);
        System.out.println("加密后的内容：" + enMsg);
        System.out.println("长度为：" + enMsg.length());

        byte[] decrypt = ECDHUtil.decrypt(enMsg, "", aPrivate.getEncoded());
        System.out.println(decrypt);


    }

    @Test
    public void testECDSA() throws Exception {
        KeyPair keyPair = me.weey.graduationproject.server.utils.KeyUtil.generateKey();
        //设置要签名的文本
        String rawData = "魏楷273351654";
        //签名
        String signature = ECDSAUtil.signature("魏楷273351654".getBytes("UTF-8"), "", keyPair.getPrivate().getEncoded());
        System.out.println("签名数据：" + signature);
        //校验
        boolean verifySignature = ECDSAUtil.verifySignature(rawData.getBytes("UTF-8"), signature, "", keyPair.getPublic().getEncoded());
        System.out.println(verifySignature);
    }

    @Test
    public void testTime() {
        String json = "{\"message\":\"username exists!\",\"statusCode\":503,\"time\":1517579499163}";
        HttpResponse httpResponse = JSON.parseObject(json, HttpResponse.class);
        System.out.println(httpResponse.getTime());
    }

    @Test
    public void testJSON() throws FileNotFoundException, UnsupportedEncodingException {
        byte[] bytes = "hshhhde中kzldcjdsafcsd国馆".getBytes("UTF-8");
        String s = JSON.toJSONString(bytes);
        System.out.println(s);
    }
}
