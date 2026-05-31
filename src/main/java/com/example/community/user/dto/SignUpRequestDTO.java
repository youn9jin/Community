package com.example.community.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


public class SignUpRequestDTO {

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식을 적어주세요.")
    private String email;

    @NotEmpty(message = "닉네임은 필수항목입니다.")
    @Size(min = 3, max = 25) //닉네임 길이 설정
    private String nickname;

    @NotEmpty(message = "비밀번호 입력은 필수항목입니다.")
    private String password;

    @URL
    private String profileImgUrl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImageUrl) {
        this.profileImgUrl = profileImageUrl;
    }
}
