package com.fm.upload.service;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.config.UploadProperties;
import com.fm.upload.web.UploadController;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    // 支持的文件类型
//    private static final List<String> ALLOW_TYPES = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    @Autowired
    private UploadProperties prop;

    @Autowired
    private FastFileStorageClient storageClient;

    public String upload(MultipartFile file) {
        try {
            // 1、图片信息校验
            // 1)校验文件类型
            String type = file.getContentType();
            if (!prop.getAllowTypes().contains(type)) {
               log.error("文件类型无效");
                throw new FmException(ExceptionEnum.INVALID_FILE_FORMAT);
            }
            // 2)校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
               log.error("上传失败，文件内容不符合要求");
                throw new FmException(ExceptionEnum.INVALID_FILE_FORMAT);
            }

            // 2、本地保存图片
            // 2.1、生成保存目录
/*            File dir = new File("D:\\FitnessMall\\upload");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2.2、保存图片
            file.transferTo(new File(dir, file.getOriginalFilename()));*/

            //保存图片到FastDFS
            String extensionName = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extensionName, null);
            //返回保存图片的完整url
            return prop.getBaseUrl() + storePath.getFullPath();

        } catch (Exception e) {
           log.error("上传文件失败！",e);
            return null;
        }
    }
}