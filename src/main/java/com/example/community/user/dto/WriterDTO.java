package com.example.community.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //이미지 도메인 구현 전 임시 장치
public class WriterDTO {
    private final Integer userId;
    private final String nickname;
    private final String profileImgUrl;
}
