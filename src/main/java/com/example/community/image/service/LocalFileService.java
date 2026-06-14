package com.example.community.image.service;

import com.example.community.global.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileService implements FileService{

    @Value("${file.upload-dir}") //yml에서 경로 주입
    private String uploadDir;

    @Override
    public String uploadFile(File file, String filename){
        try{
            //디렉토리 없으면 생성
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            //파일명 충돌 방지 -> 같은 파일이 업로드되면 덮어씌워지도록 설계
            String uniqueFilename = UUID.randomUUID() + "_" + filename;
            Path targetPath = directory.resolve(uniqueFilename);

            //파일 저장
            Files.copy(file.toPath(), targetPath);

            return uploadDir + "/" + uniqueFilename;
        } catch (IOException e){
            throw new FileStorageException("파일 저장 실패", e);
        }
    }

    @Override
    public void deleteFile(String storagePath){
        try{
            Path filePath = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("파일 삭제 실패", e);
        }
    }
}
