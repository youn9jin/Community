package com.example.community.user;

import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.dto.SignUpResponseDTO;
import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //회원가입 로직
    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {

        //1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        //2. 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException();

        }

        //3. DTO -> User 변환
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setProfileImgUrl(request.getProfileImgUrl());
        user.setStatus(UserStatus.ACTIVE);

        // 4. DB 저장
        int id = userRepository.save(user);

        // 5. ResponseDTO 구성 및 반환
        SignUpResponseDTO response = new SignUpResponseDTO();
        response.setUserId(id);
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setProfileImageUrl(user.getProfileImgUrl());
        response.setStatus(user.getStatus());

        return response;
    }

    //회원조회 로직
    public UserInfoResponseDTO getUserInfo(int userId){

        //Repository에서 user 조회
        User user = userRepository.findByID(userId);

        //user DTO로 변환
        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setProfileImageUrl(user.getProfileImgUrl());

        return dto;
    }

}
