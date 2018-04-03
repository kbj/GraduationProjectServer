package me.weey.graduationproject.server.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import me.weey.graduationproject.server.dao.inter.AvatarDao;
import me.weey.graduationproject.server.dao.inter.UploadFilRecordDao;
import me.weey.graduationproject.server.entity.Avatar;
import me.weey.graduationproject.server.entity.FileRecord;
import me.weey.graduationproject.server.entity.HttpResponse;
import me.weey.graduationproject.server.service.inter.IKeyService;
import me.weey.graduationproject.server.service.inter.IUploadService;
import me.weey.graduationproject.server.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by WeiKai on 2018/03/09.
 */
@Service
public class UploadServiceImpl implements IUploadService {

    private static final Log log = LogFactory.get();

    private final UploadFilRecordDao uploadFilRecordDao;
    private final HttpResponse httpResponse;
    private final IKeyService keyService;
    private final AvatarDao avatarDao;

    @Autowired
    public UploadServiceImpl(UploadFilRecordDao uploadFilRecordDao, HttpResponse httpResponse, IKeyService keyService, AvatarDao avatarDao) {
        this.uploadFilRecordDao = uploadFilRecordDao;
        this.httpResponse = httpResponse;
        this.keyService = keyService;
        this.avatarDao = avatarDao;
    }

    /**
     * 上传文件的记录
     * @param filePath          文件的路径
     * @param fileURLPath       文件访问地址
     */
    @Transactional
    @Override
    public void uploadFileRecord(String filePath, String fileURLPath) {
        File file = new File(filePath);
        //校验
        if (!file.exists() || StrUtil.hasEmpty(fileURLPath)) return;
        //封装好信息
        FileRecord fileRecord = new FileRecord();
        fileRecord.setFileSavePath(filePath);
        fileRecord.setFileAccessPath(fileURLPath);
        fileRecord.setFileUploadTime(new Date());
        fileRecord.setFirstDownloadTime(null);
        //新增数据库
        uploadFilRecordDao.addRecord(fileRecord);
    }

    /**
     * 删除过期的文件
     */
    @Transactional
    @Override
    public void deleteFile() {
        //查询出所有
        List<FileRecord> fileRecords = uploadFilRecordDao.findAllFiles();
        //获取当前时间
        Date now = new Date();
        for (FileRecord fileRecord : fileRecords) {
            //比较时间
            long length = DateUtil.between(now, fileRecord.getFirstDownloadTime(), DateUnit.DAY);
            if (length >= 7) {
                //删除这个文件
                File file = new File(fileRecord.getFileSavePath());
                if (file.exists()) {
                    boolean delete = file.delete();
                    if (delete) {
                        //删除成功了
                        log.info("成功删除了过期文件：" + file.getAbsolutePath());
                    } else {
                        log.info("删除过期文件失败：" + file.getAbsolutePath());
                    }
                }
                //删除数据库的记录
                int record = uploadFilRecordDao.deleteFileRecord(fileRecord.getFileSavePath());
                if (record > 0) {
                    log.info("成功删除了数据库的记录：" + fileRecord.getFileSavePath());
                } else {
                    log.info("数据库记录删除失败：" + fileRecord.getFileSavePath());
                }
            }
        }
    }

    /**
     * 根据哈希值查找文件
     */
    @Override
    public FileRecord findFile(String hashCode) {
        if (StrUtil.hasEmpty(hashCode)) return null;
        return uploadFilRecordDao.findByHash(hashCode);
    }

    /**
     * 记录第一次下载的时间
     */
    @Override
    @Transactional
    public int addFirstDownloadTime(String hash, Date date) {
        return uploadFilRecordDao.addFirstDownloadTime(hash, date);
    }

}
