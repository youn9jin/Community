package com.example.community.image.service;

import com.example.community.global.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("!prod")
public class LocalFileService implements FileService{

    private static final String PUBLIC_UPLOAD_PATH = "/uploads/";

    @Value("${file.upload-dir}") //yml에서 경로 주입
    private String uploadDir;

    @Override
    public String uploadFile(File file, String filename, StorageType storageType){
        try{
            //디렉토리 없으면 생성
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            String storedFileName = UUID.randomUUID() + getExtension(filename);
            Path targetPath = directory.resolve(storedFileName);

            //파일 저장
            Files.copy(file.toPath(), targetPath);

            return PUBLIC_UPLOAD_PATH + storedFileName;
        } catch (IOException e){
            throw new FileStorageException("파일 저장 실패", e);
        }
    }

    @Override
    public void deleteFile(String storagePath){
        try{
            Path filePath = resolveStoragePath(storagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("파일 삭제 실패", e);
        }
    }

    private Path resolveStoragePath(String storagePath) {
        String normalized = storagePath.replace('\\', '/');
        if (normalized.startsWith(PUBLIC_UPLOAD_PATH)) {
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path relativePath = Paths.get(normalized.substring(PUBLIC_UPLOAD_PATH.length())).normalize();
            Path filePath = directory.resolve(relativePath).normalize();
            if (!filePath.startsWith(directory)) {
                throw new FileStorageException("잘못된 파일 경로입니다.", new SecurityException(storagePath));
            }
            return filePath;
        }
        if (normalized.startsWith("uploads/")) {
            return resolveStoragePath("/" + normalized);
        }
        return Paths.get(storagePath).toAbsolutePath().normalize();
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex).toLowerCase();
    }
}
