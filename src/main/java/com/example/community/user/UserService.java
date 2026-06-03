package com.example.community.user;

import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.global.exception.UserNotFoundException;
import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.dto.SignUpResponseDTO;
import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        User user = new User(
                request.getEmail(),
                request.getNickname(),
                request.getPassword(),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                request.getProfileImgUrl()
        );

        // 4. DB 저장
        User savedUser = userRepository.save(user);

        // 5. ResponseDTO 구성 및 반환
        SignUpResponseDTO response = new SignUpResponseDTO();
        response.setUserId(savedUser.getUserId());
        response.setEmail(savedUser.getEmail());
        response.setNickname(savedUser.getNickname());
        response.setProfileImageUrl(savedUser.getProfileImgUrl());
        response.setStatus(savedUser.getStatus());

        return response;
    }

    //회원조회 로직
    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo(long userId){

        //Repository에서 user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        //user DTO로 변환
        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setProfileImageUrl(user.getProfileImgUrl());

        return dto;
    }

    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email){
        if(userRepository.existsByEmail(email)){
            throw new DuplicateEmailException();
        }
    }

    @Transactional(readOnly = true)
    public void checkNickNameDuplicate(String nickname){
        if(userRepository.existsByNickname(nickname)){
            throw new DuplicateNicknameException();
        }
    }

}
