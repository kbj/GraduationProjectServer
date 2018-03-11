package me.weey.graduationproject.server.entity;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 上传文件的记录
 * Created by WeiKai on 2018/03/09.
 */
@Component
public class FileRecord {
    private String fileSavePath;        //文件保存路径
    private String fileAccessPath;      //文件访问下载路径
    private Date fileUploadTime;        //文件的保存时间
    private Date firstDownloadTime;     //第一次下载的时间

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public String getFileAccessPath() {
        return fileAccessPath;
    }

    public void setFileAccessPath(String fileAccessPath) {
        this.fileAccessPath = fileAccessPath;
    }

    public Date getFileUploadTime() {
        return fileUploadTime;
    }

    public void setFileUploadTime(Date fileUploadTime) {
        this.fileUploadTime = fileUploadTime;
    }

    public Date getFirstDownloadTime() {
        return firstDownloadTime;
    }

    public void setFirstDownloadTime(Date firstDownloadTime) {
        this.firstDownloadTime = firstDownloadTime;
    }

    @Override
    public String toString() {
        return "FileRecord{" +
                "fileSavePath='" + fileSavePath + '\'' +
                ", fileAccessPath='" + fileAccessPath + '\'' +
                ", fileUploadTime=" + fileUploadTime +
                ", firstDownloadTime=" + firstDownloadTime +
                '}';
    }
}
