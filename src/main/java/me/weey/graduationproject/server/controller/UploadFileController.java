package me.weey.graduationproject.server.controller;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import me.weey.graduationproject.server.entity.FileRecord;
import me.weey.graduationproject.server.entity.User;
import me.weey.graduationproject.server.service.inter.IUploadFileService;
import me.weey.graduationproject.server.service.inter.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * 上传和下载语音或者图片文件的Controller
 * Created by WeiKai on 2018/03/08.
 */
@Controller
@RequestMapping("/file")
public class UploadFileController {

    private static final Log log = LogFactory.get();

    private final IUploadFileService uploadFileService;
    private final IUserService userService;

    @Autowired
    public UploadFileController(IUploadFileService uploadFileService, IUserService userService) {
        this.uploadFileService = uploadFileService;
        this.userService = userService;
    }

    /**
     * 上传AES后的文件，返回这个文件的下载地址
     * @param file      相对应的文件
     * @param userID    上传用户的ID
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam(value = "file") MultipartFile file,
                             String userID, HttpServletRequest request) {
        //校验
        if (StrUtil.hasEmpty(userID) || file.getSize() == 0) {
            log.error("上传文件失败！文件为空或用户ID为空");
            return "";
        }
        User user = userService.findUser(userID, userID, userID);
        if (user == null) return "";
        //将文件保存到本地
        File tempFile = new File(request.getSession().getServletContext().getRealPath("/") + "tempFile/" + RandomUtil.simpleUUID());
        try {
            if (!tempFile.getParentFile().exists()) tempFile.getParentFile().mkdirs();
            file.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "";
        }
        if (tempFile.length() == 0 || !tempFile.exists()) {
            log.error("上传文件失败！文件为空！");
            return "";
        }
        log.info("保存文件到：" + tempFile.getAbsolutePath());
        //生成这个文件的下载目录
        int apHash = HashUtil.apHash(tempFile.getAbsolutePath());
        int endIndex = request.getRequestURL().length() - request.getRequestURI().length() + 1;
        String downloadPath = request.getRequestURL().substring(0, endIndex) + "file/download/" + apHash;
        log.info("生成下载路径：" + downloadPath);
        //开启一个线程，保存数据到数据库，然后记录保存的时间，设置好定时任务
        UploadFileEndingThread uploadFileEndingThread = new UploadFileEndingThread();
        uploadFileEndingThread.setFileString(apHash + "", tempFile.getAbsolutePath());
        ThreadUtil.excAsync(uploadFileEndingThread, false);
        return downloadPath;
    }

    /**
     * 保存数据到数据库，然后记录保存的时间，设置好定时任务
     */
   private class UploadFileEndingThread implements Runnable {

        //下载路径
        private String fileString;
        //文件路径
        private String filePath;

        void setFileString(String fileString, String filePath) {
            this.fileString = fileString;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            uploadFileService.uploadFileRecord(filePath, fileString);
        }
    }

    /**
     * 提供文件的下载服务
     */
    @RequestMapping("download/{hashCode}")
    @ResponseBody
    public void downloadFile(@PathVariable String hashCode, HttpServletResponse response) {
        FileRecord fileRecord = uploadFileService.findFile(hashCode);
        if (fileRecord == null) return;
        File file = new File(fileRecord.getFileSavePath());
        if (!file.exists() || file.length() == 0) return;
        //提供下载
        OutputStream out = null;
        try {
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            out = response.getOutputStream();
            out.write(FileReader.create(file).readBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("流关闭失败！" + e.getMessage());
                }
            }
        }
        //创建线程
        AddFirstDownloadTimeThread addFirstDownloadTimeThread = new AddFirstDownloadTimeThread();
        addFirstDownloadTimeThread.setHashCode(hashCode);
        ThreadUtil.excAsync(addFirstDownloadTimeThread, false);
    }

    /**
     * 下载的时候判断是否需要添加下载时间
     */
    private class AddFirstDownloadTimeThread implements Runnable {

        private String hashCode;

        void setHashCode(String hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public void run() {
            if (StrUtil.hasEmpty(hashCode)) {
                return;
            }
            //查找
            FileRecord fileRecord = uploadFileService.findFile(hashCode);
            if (fileRecord == null) return;
            if (fileRecord.getFirstDownloadTime() == null) {
                //需要添加当前时间
                int count = uploadFileService.addFirstDownloadTime(hashCode, new Date());
                if (count > 0) {
                    log.info("添加第一次下载时间成功！" + hashCode);
                } else {
                    log.error("添加下载记录失败！");
                }
            }
        }
    }
}
