package com.example.community.image.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.UUID;

@Service
@Profile("prod")
public class S3FileService implements FileService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3FileService(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(File file, String filename, StorageType storageType) {
        String folder = switch (storageType) {
            case POST -> "image/post/";
            case PROFILE -> "image/profile/";
            case THUMBNAIL -> "image/thumbnail/";
        };
        String key = folder + UUID.randomUUID() + getExtension(filename);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));

        return "/" + key;
    }

    @Override
    public void deleteFile(String storagePath) {
        String key = storagePath.startsWith("/")
                ? storagePath.substring(1)
                : storagePath;

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex).toLowerCase();
    }
}
