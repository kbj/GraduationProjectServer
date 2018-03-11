package me.weey.graduationproject.server.dao.inter;

import me.weey.graduationproject.server.entity.FileRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 文件上传记录表的Dao
 * Created by WeiKai on 2018/03/09.
 */
@Repository
public interface UploadFilRecordDao {

    /**
     * 新增一条上传记录
     */
    int addRecord(FileRecord record);

    /**
     * 查找所有的文件
     */
    List<FileRecord> findAllFiles();

    /**
     * 根据路径删除某条记录
     */
    int deleteFileRecord(String fileSavePath);

    /**
     * 根据hash值查找某条记录
     */
    FileRecord findByHash(String hashCode);

    /**
     * 记录第一次下载的时间
     */
    int addFirstDownloadTime(@Param("fileAccessPath") String fileAccessPath, @Param("time") Date time);
}
