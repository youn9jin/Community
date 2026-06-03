package com.example.community.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@RequiredArgsConstructor
public class SignUpRequestDTO {

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식을 적어주세요.")
    private final String email;

    @NotEmpty(message = "닉네임은 필수항목입니다.")
    @Size(min = 3, max = 25) //닉네임 길이 설정
    private final String nickname;

    @NotEmpty(message = "비밀번호 입력은 필수항목입니다.")
    private final String password;

    @URL
    private final String profileImgUrl;
}
