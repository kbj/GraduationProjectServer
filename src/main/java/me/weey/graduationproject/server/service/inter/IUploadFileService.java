package me.weey.graduationproject.server.service.inter;


import me.weey.graduationproject.server.entity.FileRecord;

import java.util.Date;

/**
 * 上传文件的Service接口
 * Created by WeiKai on 2018/03/09.
 */
public interface IUploadFileService {

    /**
     * 数据库记录文件的Service接口
     */
    void uploadFileRecord(String filePath, String fileHash);

    /**
     * 删除那些已经过期的任务和文件
     */
    void deleteFile();

    /**
     * 查找文件
     */
    FileRecord findFile(String hashCode);

    /**
     * 新增第一次下载的时间记录
     */
    int addFirstDownloadTime(String hash, Date date);
}
