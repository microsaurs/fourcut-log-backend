package com.flog.fourcut_log.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_BAS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "NICKNAME", nullable = false, length = 50)
    private String nickname;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "LGIN_TYPE", nullable = false, length = 20)
    private String lginType;

    @Column(name = "ROLE", nullable = false, length = 20)
    private String role;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Builder
    public User(String nickname, String email, String lginType, String role) {
        this.nickname = nickname;
        this.email = email;
        this.lginType = lginType;
        this.role = role;
        this.status = "N";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 사용자 정보 업데이트 (OAuth2 로그인시 사용)
     */
    public User update(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /**
     * 소셜 로그인 사용자인지 확인
     */
    public boolean isSocialUser() {
        return "kakao".equals(this.lginType) || "naver".equals(this.lginType);
    }
}
