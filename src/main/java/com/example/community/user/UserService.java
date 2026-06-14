package com.example.community.user;

import com.example.community.auth.RefreshTokenRepository;
import com.example.community.global.exception.BadRequestException;
import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.global.exception.ForbiddenException;
import com.example.community.global.exception.ImageNotFoundException;
import com.example.community.global.exception.UserNotFoundException;
import com.example.community.image.Image;
import com.example.community.image.ImageRepository;
import com.example.community.image.service.ImageService;
import com.example.community.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;


    //회원가입 로직
    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO request, MultipartFile file) {

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

        String profileImgUrl = null;
        if (file != null && !file.isEmpty()) {
            Image image = imageService.processAndSaveProfileImage(file, savedUser);
            profileImgUrl = image.getStoragePath();
        }

        // 5. ResponseDTO 구성 및 반환
        return new SignUpResponseDTO(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getStatus(),
                profileImgUrl
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

        String profileImgUrl = imageRepository.findByUserUserIdAndActiveTrue(userId)
                .map(image -> image.getStoragePath())
                .orElse(null);

        //user DTO로 변환
        return new UserInfoResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                profileImgUrl
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

        if (request.getImageId() != null) {
            imageRepository.findByUserUserIdAndActiveTrue(userId)
                    .ifPresent(existing -> existing.deactivate());
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ImageNotFoundException("Image not found."));
            if (!image.getUploadedBy().getUserId().equals(userId)) {
                throw new ForbiddenException("You are not authorized to use this image.");
            }
            if (image.isActive()) {
                throw new BadRequestException("Image is already in use.");
            }
            image.attachToUser(user);
        }

        String profileImgUrl = imageRepository.findByUserUserIdAndActiveTrue(userId)
                .map(image -> image.getStoragePath())
                .orElse(null);

        return new UserInfoResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                profileImgUrl
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
        refreshTokenRepository.deleteByUser(user);
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
            throw new BadRequestException("current password does not match");
        }

        // 4. 새 비밀번호가 기존 비밀번호와 동일한지 검증
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("new password must be different");
        }

        // 5. 새 비밀번호 수정 및 저장
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        // 6. 해당 유저의 모든 리프레시 토큰 폐기 (기존 세션 무효화)
        refreshTokenRepository.deleteByUser(user);

    }

}
