package com.flog.fourcut_log.global.config;

import com.flog.fourcut_log.auth.service.CustomOAuth2UserService;
import com.flog.fourcut_log.global.config.handler.CustomAccessDeniedHandler;
import com.flog.fourcut_log.global.config.handler.CustomAuthenticationEntryPoint;
import com.flog.fourcut_log.global.config.handler.OAuth2SuccessHandler;
import com.flog.fourcut_log.global.jwt.JwtAuthenticationFilter;
import com.flog.fourcut_log.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable)
            .authorizeHttpRequests(authorizeRequest ->
                    authorizeRequest.requestMatchers(
                                    AntPathRequestMatcher.antMatcher("/auth/**"),
                                    AntPathRequestMatcher.antMatcher("/login/**"),
                                    AntPathRequestMatcher.antMatcher("/oauth2/**"),
                                    AntPathRequestMatcher.antMatcher("/token/reissue")
                            ).permitAll()
                            .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 ->
                    oauth2.userInfoEndpoint(userInfoEndpointConfig ->
                                    userInfoEndpointConfig.userService(customOAuth2UserService))
                            .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }
}
