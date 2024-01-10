package com.lihong.csuftcampus.utils;

import cn.hutool.core.date.DateUtil;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

/**
 * minio文件上传工具类
 */
@Component
public class MinioUtils {
    @Autowired
    private MinioProperties minioProperties;

    public String upload(MultipartFile file) {
        try {
            //创建一个minioClient对象
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minioProperties.getEndpointUrl())
                            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                            .build();

            //创建bucket
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build());
            } else {
                System.out.println("Bucket 'csuftcampus-bucket' already exists.");
            }


            //获取上传文件名称
            //1 每个上传文件名称唯一
            //2 根据当前日期对上传文件进行分组
            String dateDir = DateUtil.format(new Date(), "yyyyMMdd");
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String filename = dateDir + "/" + uuid + originalFilename;


            //文件上传
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());

            //获取上传文件在minio的路径
            return minioProperties.getServerUrl() + "/" + minioProperties.getBucketName() + "/" + filename;
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}
