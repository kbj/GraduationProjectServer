package me.weey.graduationproject.server.service.impl;

import me.weey.graduationproject.server.dao.inter.KeyDao;
import me.weey.graduationproject.server.service.inter.IKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务器端Key相关Service的实现类
 * Created by WeiKai on 2018/01/25.
 */
@Service
public class KeyServiceImpl implements IKeyService {

    @Autowired
    private KeyDao keyDao;

    /**
     * 获取public key
     */
    @Transactional(readOnly = true)
    @Override
    public String getPublicKey() {
        return keyDao.findKey().getPublicKey();
    }

    /**
     * 获取private key
     */
    @Transactional(readOnly = true)
    @Override
    public String getPrivateKey() {
        return keyDao.findKey().getPrivateKey();
    }
}
