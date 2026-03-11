package com.flog.fourcut_log.user.repository;

import com.flog.fourcut_log.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 찾기
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임으로 사용자 찾기
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 닉네임 존재 여부 확인
     */
    boolean existsByNickname(String nickname);

    /**
     * 로그인 타입별 사용자 목록 조회
     */
    List<User> findByLginType(String lginType);

    /**
     * 특정 권한을 가진 사용자 목록 조회
     */
    List<User> findByRole(String role);
}
