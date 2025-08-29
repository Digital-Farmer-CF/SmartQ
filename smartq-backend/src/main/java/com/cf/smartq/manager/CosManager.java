package com.cf.smartq.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.cf.smartq.config.CosClientConfig;
import java.io.File;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 *
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        
        // 创建元数据对象
        ObjectMetadata metadata = new ObjectMetadata();
        
        // 设置文件长度
        metadata.setContentLength(file.length());
        
        // 根据文件扩展名设置Content-Type
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            metadata.setContentType("image/png");
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            metadata.setContentType("image/jpeg");
        } else if (fileName.endsWith(".gif")) {
            metadata.setContentType("image/gif");
        } else if (fileName.endsWith(".webp")) {
            metadata.setContentType("image/webp");
        } else if (fileName.endsWith(".svg")) {
            metadata.setContentType("image/svg+xml");
        } else {
            metadata.setContentType("application/octet-stream");
        }
        
        // 设置元数据到请求中
        putObjectRequest.setMetadata(metadata);
        
        return cosClient.putObject(putObjectRequest);
    }
}
