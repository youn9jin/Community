package com.example.community.user;

import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.global.exception.UserNotFoundException;
import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.dto.SignUpResponseDTO;
import com.example.community.user.dto.UpdateUserRequestDTO;
import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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
                passwordEncoder.encode(request.getPassword()) //암호화해서 저장
        );

        // 4. DB 저장
        User savedUser = userRepository.save(user);

        // 5. ResponseDTO 구성 및 반환
        return new SignUpResponseDTO(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getStatus()
        );
    }

    //회원조회 로직
    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo(Integer userId){

        //Repository에서 user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getDeletedAt() != null) {
            throw new UserNotFoundException(userId);
        }

        //user DTO로 변환
        return new UserInfoResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                null
        );
    }

    //회원 정보 수정 메서드
    @Transactional
    public UserInfoResponseDTO updateUserInfo(Integer userId, UpdateUserRequestDTO request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getDeletedAt() != null) {
            throw new UserNotFoundException(userId);
        }

        if (request.getNickname() != null
                && !request.getNickname().equals(user.getNickname())
                && userRepository.existsByNicknameAndUserIdNot(request.getNickname(), userId)) {
            throw new DuplicateNicknameException();
        }

        if (request.getNickname() != null) {
            user.update(request.getNickname(), user.getPassword());
        }

        return new UserInfoResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                null
        );
    }

    //이메일 중복 체크 메서드
    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email){
        if(userRepository.existsByEmail(email)){
            throw new DuplicateEmailException();
        }
    }

    //닉네임 중복 체크 메서드
    @Transactional(readOnly = true)
    public void checkNickNameDuplicate(String nickname){
        if(userRepository.existsByNickname(nickname)){
            throw new DuplicateNicknameException();
        }
    }

    //회원 탈퇴 메서드
    @Transactional
    public void deleteUser(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        //이미 탈퇴된 유저 처리
        if(user.getDeletedAt() != null){
            throw new UserNotFoundException(userId);
        }

        user.softDelete();
    }

}
