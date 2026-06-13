package com.example.community.image.service;

import com.example.community.global.exception.BadRequestException;
import com.example.community.global.exception.ForbiddenException;
import com.example.community.global.exception.ImageNotFoundException;
import com.example.community.image.Image;
import com.example.community.image.ImageRepository;
import com.example.community.image.ImageType;
import com.example.community.image.dto.ImageUploadResponseDTO;
import com.example.community.user.User;
import com.example.community.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.community.image.dto.ImageUploadRequestDTO.MAX_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileService fileService;
    private final ImageRepository repository;
    private final ImageProcessor processor;
    private final UserRepository userRepository;

    @Transactional
    public ImageUploadResponseDTO uploadPostImage(MultipartFile file, Integer loginUserId) {
        return upload(file, ImageType.POST, loginUserId);
    }

    @Transactional
    public ImageUploadResponseDTO uploadProfileImage(MultipartFile file, Integer loginUserId) {
        return upload(file, ImageType.PROFILE, loginUserId);
    }

    public Image processAndSaveProfileImage(MultipartFile file, User uploader) {
        validateFile(file);
        ImageProcessor.ProcessedFiles processedFiles = processor.processImage(file, ImageType.PROFILE);
        try {
            String originalFilename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename() : "unknown";
            String baseName = stripExtension(originalFilename);
            String jpgPath = fileService.uploadFile(processedFiles.getJpgFile(), baseName + ".jpg");
            String webpPath = processedFiles.getWebpFile() != null
                    ? fileService.uploadFile(processedFiles.getWebpFile(), baseName + ".webp")
                    : null;
            Image image = Image.createOrphan(jpgPath, webpPath, uploader);
            image.attachToUser(uploader);
            return repository.save(image);
        } finally {
            processedFiles.getJpgFile().delete();
            if (processedFiles.getWebpFile() != null) {
                processedFiles.getWebpFile().delete();
            }
        }
    }

    @Transactional
    public void deleteImage(Integer imageId, Integer loginUserId) {
        Image image = repository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found."));

        if (!image.getUploadedBy().getUserId().equals(loginUserId)) {
            throw new ForbiddenException("You are not authorized to delete this image.");
        }

        fileService.deleteFile(image.getStoragePath());
        if (image.getThumbnailPath() != null) {
            fileService.deleteFile(image.getThumbnailPath());
        }

        repository.delete(image);
    }

    private ImageUploadResponseDTO upload(MultipartFile file, ImageType type, Integer loginUserId){

        //1. 파일 검증
        validateFile(file);

        //2. image 변환
        ImageProcessor.ProcessedFiles processedFiles
                = processor.processImage(file, type);


        try{
            //3. 파일 저장
            String originalFilename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "unknown";
            String baseName = stripExtension(originalFilename);

            String jpgPath  = fileService.uploadFile(
                    processedFiles.getJpgFile(), baseName + ".jpg");
            String webpPath = processedFiles.getWebpFile() != null
                    ? fileService.uploadFile(processedFiles.getWebpFile(), baseName + ".webp")
                    : null;

            // 4. uploadedBy 조회
            User uploader = userRepository.findById(loginUserId)
                    .orElseThrow(() -> new BadRequestException("User not found"));

            //5. DB에 고아 상태로 저장
            Image image = Image.createOrphan(jpgPath, webpPath, uploader);
            repository.save(image);

            return new ImageUploadResponseDTO(
                    image.getImageId(),
                    image.getStoragePath(),
                    image.getThumbnailPath(),
                    image.isActive(),
                    image.getCreatedAt(),
                    image.getUploadedBy().getUserId()
            );
        } finally {
            // ImageProcessing 과정에서 생성한 임시 디렉토리 삭제
            processedFiles.getJpgFile().delete();
            if (processedFiles.getWebpFile() != null) {
                processedFiles.getWebpFile().delete();
            }
        }
    }

    private void validateFile(MultipartFile file){
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size cannot exceed 10MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new BadRequestException("Unsupported image format. (jpg, png, webp)");
        }
    }

    private boolean isValidContentType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png")  ||
                contentType.equals("image/webp");
    }

    private String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }
}
