package com.example.community.user;

import com.example.community.auth.RefreshTokenRepository;
import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.global.exception.UserNotFoundException;
import com.example.community.user.dto.*;
import lombok.RequiredArgsConstructor;
import com.example.community.global.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;


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
            user.updateNickname(request.getNickname());
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

    @Transactional
    public void changePassword(Integer userId, PasswordChangeRequestDTO request) {

        //1. 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. 탈퇴한 사용자의 비밀번호 변경 차단
        if (user.getDeletedAt() != null) {
            throw new UserNotFoundException(userId);
        }

        // 3. 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException();
        }

        // 4. 새 비밀번호가 기존 비밀번호와 동일한지 검증
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException();
        }

        // 5. 새 비밀번호 수정 및 저장
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        // 6. 해당 유저의 모든 리프레시 토큰 폐기 (기존 세션 무효화)
        refreshTokenRepository.deleteByUser(user);

    }


}
