package me.weey.graduationproject.server.service;

import me.weey.graduationproject.server.service.inter.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务，检查上传文件超过一天的自动删除文件和数据库记录
 * Created by WeiKai on 2018/03/09.
 */
@Component
public class UploadFileSchedule {

    private final IUploadService uploadFileService;

    @Autowired
    public UploadFileSchedule(IUploadService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    /**
     *  一天执行一次，删除那些已经过期的文件
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void scanFile() {
        uploadFileService.deleteFile();
    }
}
