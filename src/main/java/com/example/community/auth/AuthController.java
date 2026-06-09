package com.example.community.auth;

import com.example.community.auth.dto.LoginRequestDTO;
import com.example.community.auth.dto.LoginResponseDTO;
import com.example.community.global.ResponseWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    //cookie 생성 로직
    private ResponseCookie buildRtCookie(String value, int maxAge) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true) // HttpOnly 설정
                .secure(false) //HTTPS에서만 쿠키가 전송되도록 설정 -> 추후 배포시 true로 변경
                .sameSite("Strict") //외부 사이트 위조 요청에 쿠키 전송 통제
                .path("/auth") // 쿠키 전송 경로 설정
                .maxAge(maxAge)
                .build();
    }

    // 로그인 처리 및 Access Token, Refresh Token 발급
    @PostMapping
    public ResponseEntity<ResponseWrapper<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) { //cookie 설정용 HTTPServletResponse

        AuthService.LoginResult result = authService.login(request);

        ResponseCookie rtCookie = buildRtCookie(result.getRefreshToken(), 60 * 60 * 24 * 14);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                .body(ResponseWrapper.success("Login_success", result.getLoginResponse()));
    }

    // Refresh Token 검증 및 새 토큰 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseWrapper<LoginResponseDTO>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken)  { // Cookie에서 RT 꺼냄

        AuthService.LoginResult result = authService.refresh(refreshToken);

        ResponseCookie rtCookie = buildRtCookie(result.getRefreshToken(), 60 * 60 * 24 * 14);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                .body(ResponseWrapper.success("Token_refresh_success", result.getLoginResponse()));
    }

    // 현재 인증된 사용자의 Refresh Token 삭제 및 로그아웃 처리
    @DeleteMapping
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        authService.logout(userId);

        // Cookie 삭제: 만료시간을 0으로 설정해 즉시 만료 및 브라우저에서 삭제하도록 함
        ResponseCookie rtCookie = buildRtCookie("", 0);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                .build();
    }
}
