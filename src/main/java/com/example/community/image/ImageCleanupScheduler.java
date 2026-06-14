package com.example.community.image;

import com.example.community.image.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @Transactional
    public void cleanUpOrphanImages() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24); //현재 시간 기준 24시간 전
        List<Image> orphans = imageRepository.findByActiveFalseAndCreatedAtBefore(threshold);

        for (Image image : orphans) {
            fileService.deleteFile(image.getStoragePath()); //파일 시스템에서 삭제
            if (image.getThumbnailPath() != null) {
                fileService.deleteFile(image.getThumbnailPath()); //썸네일 경로도 삭제
            }
        }

        imageRepository.deleteAll(orphans); // DB record 삭제
        log.info("[ImageCleanup] 고아 이미지 {}건 삭제 완료", orphans.size());
    }
}
