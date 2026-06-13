package com.example.community.image.service;


import com.example.community.global.exception.ImageProcessingException;
import com.example.community.image.ImageType;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import net.coobird.thumbnailator.Thumbnails; // 이미지 리사이즈/압축용 라이브러리
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.example.community.global.exception.BadRequestException;

@Component
public class ImageProcessor {

    private static final int PROFILE_THUMBNAIL_WIDTH = 150; // 헤더 프로필 표시용

    //DTO 역할, 데이터 운반 객체
    public static class ProcessedFiles {

        private final File jpgFile;
        private final File webpFile; // POST는 null

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
                throw new BadRequestException("invalid image file");
            }

            // 3. 타입별 압축률 결정
            float quality = getCompressionQuality(type);

            // 4. 변환
            File jpgFile  = compressToJPG(inputImage, quality);

            // POST는 썸네일 없음 → null
            File webpFile = (type == ImageType.PROFILE)
                    ? convertToThumbnailWebP(inputImage, quality)
                    : null;

            return new ProcessedFiles(jpgFile, webpFile);

        } catch (IOException e) {
            throw new ImageProcessingException("image transition failed", e);
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

    // PROFILE 전용: 150px 리사이즈 + WebP 변환
    private File convertToThumbnailWebP(BufferedImage image, float quality) throws IOException {
        BufferedImage resized = Thumbnails.of(image)
                .width(PROFILE_THUMBNAIL_WIDTH)
                .keepAspectRatio(true)
                .asBufferedImage();
        try {
            return writeWebP(resized);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private File writeWebP(BufferedImage image) throws Exception {
        File output = File.createTempFile("webp_", ".webp");
        ImmutableImage.fromAwt(image)
                .output(WebpWriter.DEFAULT, output);
        return output;
    }
}
