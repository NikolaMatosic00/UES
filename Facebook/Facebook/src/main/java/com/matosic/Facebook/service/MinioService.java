package com.matosic.Facebook.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Service
@Slf4j
public class MinioService {

    private String minioUrl = "http://localhost:9000";

    private String accessKey = "minioadmin";
//
//    @Value("${minio.secretKey}")
    private String secretKey = "minioadmin";

    private String bucketName = "facebook";

    private MinioClient minioClient;

    public MinioService() {
        try {
            this.minioClient = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();
            log.info("MinioClient initialized with URL: {}", minioUrl);
        } catch (Exception e) {
            log.error("Error initializing MinioClient with URL: {}", minioUrl, e);
            // Handle exception or rethrow
            throw new RuntimeException("Error initializing MinioClient", e);
        }
    }

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        log.info("Uploading file: {} to MinIO bucket: {}", fileName, bucketName);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                            file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            log.info("File: {} uploaded successfully to MinIO bucket: {}", fileName, bucketName);
        } catch (Exception e) {
            log.error("Error uploading file: {} to MinIO", fileName, e);
            throw new IOException("Error uploading file to MinIO", e);
        }

        return minioUrl + "/" + bucketName + "/" + fileName;
    }
}
