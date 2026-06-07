package com.example.community.auth;

import com.example.community.auth.dto.LoginRequestDTO;
import com.example.community.auth.dto.LoginResponseDTO;
import com.example.community.global.JwtProvider;
import com.example.community.global.exception.UnauthorizedException;
import com.example.community.user.User;
import com.example.community.user.UserRepository;
import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    //1. 로그인
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. 이메일 검증
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException());

        // 2. 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new UnauthorizedException();
        }

        // 3. 기존 RT 삭제 (재로그인 시 기존 RT 무효화)
        refreshTokenRepository.deleteByUser(user);

        //4. 새 AT 생성
        String accessToken = jwtProvider.createAccessToken(user.getUserId());

        //5.  RT 생성 (UUID 문자열)
        String rawRefreshToken = jwtProvider.createRefreshToken();

        //6. RT DB 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(rawRefreshToken)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();
        refreshTokenRepository.save(refreshToken);

        // 7. AT + RT 반환
        return new LoginResponseDTO(
                new UserInfoResponseDTO(
                        user.getUserId(),
                        user.getEmail(),
                        user.getNickname()
                ),
                accessToken
        );
    }

    //2. RT 토큰 재발급(RTR)
    public LoginResponseDTO refresh(String rawRefreshToken){

        // 1. DB에서 RT 조회
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(rawRefreshToken)
                .orElseThrow(() -> new UnauthorizedException());

        //2. 만료 확인
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken); // 만료된 RT는 DB에서 삭제
            throw new UnauthorizedException(); //재로그인 하라고 client에게 전송
        }

        //3. 유저 조회
        User user = refreshToken.getUser();

        // 4. 기존 RT 삭제 (RTR 핵심: 한 번 쓰면 폐기)
        refreshTokenRepository.delete(refreshToken);

        // 5. 새 AT 생성
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());

        // 6. 새 RT 생성 + 저장
        String newRawRefreshToken = jwtProvider.createRefreshToken();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(newRawRefreshToken)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // 7. 새 AT + 새 RT 반환
        return new LoginResponseDTO(
                new UserInfoResponseDTO(
                        user.getUserId(),
                        user.getEmail(),
                        user.getNickname()
                ),
                newAccessToken
        );

    }


    //3. 로그아웃
    public void logout(Integer userId){

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UnauthorizedException());

        refreshTokenRepository.deleteByUser(user);
    }

}
