package com.example.demo.config;

import com.example.demo.domain.user.entity.UserRoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 스프링 부트가 Config 클래스인 것을 인식하도록 하는 어노테이션
@Configuration
// 시큐리티 설정 활성화 위한 어노테이션
@EnableWebSecurity
public class SecurityConfig {

    // 스프링 시큐리티에서는 로그인 과정(인증)에서 비밀번호를 암호화해서 저장해야 한다. 아래는 이 때 사용할 암호화 클래스.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 role 수직적 계층 적용
    @Bean
    public RoleHierarchy roleHierarchy() {

        // 로그인 시 스프링 시큐리티가 ADMIN이 USER보다 높다는 것을 인식하도록 implies를 통해 적용
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.toString()).implies(UserRoleType.USER.toString())
                .build();
    }

    // 시큐리티 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 시큐리티는 csrf 보안이 설정되어 있는데 개발 환경에서는 복잡해지기 때문에 해제
        http.csrf(csrf -> csrf.disable());

        // 접근 경로별 인가 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/user/join").permitAll()
                .requestMatchers("/user/update/**").hasRole("USER")
                .requestMatchers("/**").permitAll());

        // 로그인 방식 설정 : form 로그인 방식
        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

}
