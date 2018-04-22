package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //extension name
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("Starting uploading file , the uploaded file name is: {},the uploading path is: {},the new FileName is: {}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            //fileDir.mkdir();
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
            //success uploading,start uploading to ftp!
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //finishing updating!
            //delete file in upload
            targetFile.delete();

        } catch (IOException e) {
            logger.error("uploading file exception!");
            return null;
        }

        return targetFile.getName();

    }
}
