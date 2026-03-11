package com.flog.fourcut_log.user.service;

import com.flog.fourcut_log.user.entity.User;
import com.flog.fourcut_log.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 로그인시 사용자 저장 또는 업데이트
     */
    public User saveOrUpdateOAuth2User(User newUser) {
        User user = userRepository.findByEmail(newUser.getEmail())
                .map(existingUser -> existingUser.update(newUser.getNickname()))
                .orElse(newUser);

        return userRepository.save(user);
    }

    /**
     * 이메일로 사용자 찾기
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다: " + email));
    }
}
