package com.example.community.image.service;

import java.io.File;

public interface FileService {

    // ImageProcessor가 반환한 File 저장용
    String uploadFile(File file, String filename);

    // 삭제
    void deleteFile(String storagePath);
}