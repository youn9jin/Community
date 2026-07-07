package com.example.community.image.service;

import com.example.community.image.service.StorageType;

import java.io.File;

public interface FileService {

    // ImageProcessor가 반환한 File 저장용
    String uploadFile(File file, String filename, StorageType storageType);

    // 삭제
    void deleteFile(String storagePath);
}
