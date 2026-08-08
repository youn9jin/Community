package com.example.community.image;

import java.util.List;

public interface ImageRepositoryCustom {
    List<Image> findByUserUserIdInAndActiveTrue(List<Integer> userIds);
}
