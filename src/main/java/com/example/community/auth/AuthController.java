package com.example.community.auth;

import com.example.community.auth.dto.LoginRequestDTO;
import com.example.community.auth.dto.LoginResponseDTO;
import com.example.community.global.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인 처리 및 Access Token, Refresh Token 발급
    @PostMapping
    public ResponseEntity<ResponseWrapper<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("Login_success", response));
    }

    // Refresh Token 검증 및 새 토큰 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseWrapper<LoginResponseDTO>> refresh(
            @RequestParam String refreshToken) {

        LoginResponseDTO response = authService.refresh(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("Token_refresh_success", response));
    }

    // 현재 인증된 사용자의 Refresh Token 삭제 및 로그아웃 처리
    @DeleteMapping
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}
