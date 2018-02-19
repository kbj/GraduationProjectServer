package me.weey.graduationproject.server.service.inter;

/**
 * 服务器端有关Key处理的Service interface
 * Created by WeiKai on 2018/01/25.
 */
public interface IKeyService {

    /**
     * 获取服务器的public key
     */
    public String getPublicKey();

    /**
     * 获取服务器的private key
     */
    public String getPrivateKey();
}
