package com.example.community.image.service;


import com.example.community.global.exception.ImageProcessingException;
import com.example.community.image.ImageType;
import com.luciad.imageio.webp.WebPWriteParam; //WebP 변환용 라이브러리
import net.coobird.thumbnailator.Thumbnails; // 이미지 리사이즈/압축용 라이브러리
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.example.community.global.exception.BadRequestException;

@Component
public class ImageProcessor {


    //DTO 역할, 데이터 운반 객체
    public static class ProcessedFiles {

        private final File jpgFile;
        private final File webpFile;

        public ProcessedFiles(File jpg, File webp) {
            this.jpgFile = jpg;
            this.webpFile = webp;
        }

        public File getJpgFile()  { return jpgFile; }
        public File getWebpFile() { return webpFile; }
    }

    public ProcessedFiles processImage(MultipartFile file, ImageType type) {
        try {
            // 1. 스트림에서 BufferedImage로 디코딩
            BufferedImage inputImage = ImageIO.read(file.getInputStream());

            // 2. null 체크
            if (inputImage == null) {
                throw new BadRequestException();
            }

            // 3. 타입별 압축률 결정
            float quality = getCompressionQuality(type);

            // 4. 변환
            File jpgFile  = compressToJPG(inputImage, quality);
            File webpFile = convertToWebP(inputImage, quality);

            return new ProcessedFiles(jpgFile, webpFile);

        } catch (IOException e) {
            throw new ImageProcessingException("이미지 변환 실패", e);
        }
    }

    private float getCompressionQuality(ImageType type) {
        return switch (type) {
            case PROFILE -> 0.6f;  // 프로필은 작게 보이므로 더 압축
            case POST    -> 0.8f;
        };
    }

    private File compressToJPG(BufferedImage image, float quality) throws IOException {

        // 1. 임시 파일 생성
        File tempFile = File.createTempFile("img_", ".jpg");

        // 2. Thumbnailator로 압축 후 저장
        Thumbnails.of(image)
                .scale(1.0)            // ③ 크기 변경 없음
                .outputFormat("jpg")
                .outputQuality(quality) // ④ 0.0 ~ 1.0
                .toFile(tempFile);

        return tempFile;
    }

    private File convertToWebP(BufferedImage image, float quality) throws IOException {

        File tempFile = File.createTempFile("img_", ".webp");

        // 1. webp-imageio의 writer 가져오기
        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

        // 2. WebP 저장 옵션 객체 생성
        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        // 3. 압축 설정 지정
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // ③
        writeParam.setCompressionType(
                writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION] // ④
        );

        writeParam.setCompressionQuality(quality);

        // ⑤ try-with-resources로 스트림 자동 닫기
        try (FileImageOutputStream output = new FileImageOutputStream(tempFile)) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }

        return tempFile;
    }
}
