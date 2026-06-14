package com.example.community.image;

public final class ImageUrlUtils {

    private ImageUrlUtils() {
    }

    public static String toPublicUrl(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return storagePath;
        }
        if (storagePath.startsWith("http://") || storagePath.startsWith("https://")) {
            return storagePath;
        }

        String normalized = storagePath.replace('\\', '/');
        if (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }
}
