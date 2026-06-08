package com.example.community.global.filter;

import com.example.community.global.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // 필터 제외 경로
    private static final String[] EXCLUDED_PATHS = {
            "/error"               // 에러 페이지
    };

    // 필터 제외 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("POST".equals(method) && ("/auth".equals(path) || "/auth/refreshToken".equals(path))) {
            return true;
        }

        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    // 실제 필터링 로직 - 1차 분기
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {

        // 1. 토큰 추출
        Optional<String> token = extractToken(request);

        // 2. 토큰 없음 → 그냥 통과
        if (token.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 토큰 검증 및 SecurityContext 저장
        if (!validateAndSetAuthentication(token.get())) {
            writeUnauthorizedResponse(response);
            return;
        }

        chain.doFilter(request, response);
    }

    // 토큰 추출 (헤더 우선, 쿠키 다음) 메서드
    private Optional<String> extractToken(HttpServletRequest request) {
        return extractTokenFromHeader(request)
                .or(() -> extractTokenFromCookie(request));
    }

    // 헤더에서 토큰 추출
    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    // 쿠키에서 토큰 추출
    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // 토큰 검증 및 SecurityContext 저장
    private boolean validateAndSetAuthentication(String token) {
        try {
            if (!jwtProvider.validate(token)) {
                return false;
            }

            Integer userId = jwtProvider.getUserId(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of() // 권한 없음
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"error":{"code":401,"message":"Unauthorized request","status":"UNAUTHORIZED"}}
                """);
    }
}
