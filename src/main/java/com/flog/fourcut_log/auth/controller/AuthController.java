package com.flog.fourcut_log.auth.controller;

import com.flog.fourcut_log.auth.model.dto.TokenResponse;
import com.flog.fourcut_log.global.jwt.JwtUtil;
import com.flog.fourcut_log.global.model.ApiResponse;
import com.flog.fourcut_log.global.model.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String bearerToken) {
        // 1. Access Token 유효성 검증
        String accessToken = bearerToken.replace("Bearer ", "");
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "유효하지 않은 Access Token입니다."));
        }

        // 2. Redis에서 Refresh Token 삭제
        String email = jwtUtil.getUsername(accessToken);
        redisTemplate.delete(email);

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."));
        }

        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 2. Redis에 저장된 Refresh Token과 일치 여부 확인
        String storedRefreshToken = redisTemplate.opsForValue().get(email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다."));
        }

        // 3. 새 토큰 발급 (Refresh Token Rotation - 보안을 위해 둘 다 새로 발급)
        String newAccessToken = jwtUtil.createAccessToken(email, role);
        String newRefreshToken = jwtUtil.createRefreshToken(email, role);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, tokenResponse));
    }
}
