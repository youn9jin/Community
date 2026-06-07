package com.example.community.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j // 로그 출력용
@Component //Spring Bean으로 등록
public class JwtProvider {

    private final SecretKey secretKey; //JWT 서명 및 검증에 활용되는 암호화 keyu
    private final int accessExpiration; // AT 만료시간
    private final int refreshExpiration; // RT 만료시간

    public JwtProvider(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_ACCESS_EXPIRATION}") int accessExpiration,
            @Value("${JWT_REFRESH_EXPIRATION}") int refreshExpiration
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //키 객체 생성
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(Integer userId){
        Date now = new Date();

        //현재 시각 + 만료 시간 = 만료 시각
        Date expiryDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(userId.toString()) //토큰 사용자에 대한 식별자 지정
                .issuedAt(now) // 발급 시간
                .expiration(expiryDate) //만료 시간
                .signWith(secretKey) //secretKey 값으로 서명 -> 위조 적발용
                .compact(); //압축 문자열 최종 변환
    }

    public String createRefreshToken(){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }


    //내부 공통 파싱해 payload 정보 확인
    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Integer getUserId(String token) {
        return Integer.valueOf(
                parse(token).getPayload().getSubject()
        );
    }

}
