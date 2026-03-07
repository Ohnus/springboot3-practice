package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequestDto;
import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.entity.UserRoleType;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
// 스프링 컨테이너로부터 bean으로 등록된 객체를 매개변수로 받아와서 생성자 주입
@RequiredArgsConstructor
// 클래스 전역에 기본 트랜잭션 설정(조회 전용 트랜잭션)
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 유저 접근 권한 체크
    public Boolean isAccess(String username) {

        // 시큐리티로부터 현재 로그인 되어 있는 유저의 username 추출
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 시큐리티로부터 현재 로그인 되어 있는 유저의 role 추출
        // 로그인 시 ROLE_ 접두사 붙은 계급이 객체로 저장되어 있음
        String sessionRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        // iterator().next()는 권한이 여러 개일 때 첫 번째만 가져오므로 아래처럼 전체 권한 확인하는 방식이 더 안전함
        //boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        //        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // 수직적으로 ADMIN이면 모든 유저에 대해 삭제, 수정 무조건 접근 가능
        if ("ROLE_ADMIN".equals(sessionRole)) return true;

        // 수평적으로 특정 행위를 수행할 username에 대해 세션 username과 같은지 체크
        if (username.equals(sessionUsername)) return true;

        // 그외 모두 불가
        return false;
    }

    // 회원 가입
    // 해당 트랜잭션은 readOnly = false로 조회+수정 가능한 트랜잭션
    @Transactional // 데이터를 다루는 메서드라면 트랜잭션은 꼭 달자.
    public void join(UserRequestDto userRequestDto) {

        // 중복 체크
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(userRequestDto.getPassword());

        // Builder로 Entity 생성
        UserEntity userEntity = UserEntity.builder()
                .username(userRequestDto.getUsername())
                .password(encodedPassword)
                .nickname(userRequestDto.getNickname())
                .role(UserRoleType.USER)
                .build();

        // entity 저장
        userRepository.save(userEntity);
    }

    // 유저 조회
    public UserResponseDto getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        // entity를 dto에 넘겨주고 builder를 통해 변환하여 클라이언트에게 전달
        return UserResponseDto.from(userEntity);
    }

    // 유저 전체 조회
    public List<UserResponseDto> getAllUsers() {
        // 전체 유저 entity 매핑
        List<UserEntity> list = userRepository.findAll();

        // 각각의 유저 entity를 ResponseDto 리스트에 담기
        List<UserResponseDto> dtos = new ArrayList<>();
        for (UserEntity userEntity : list) {
            dtos.add(UserResponseDto.from(userEntity));
        }

        return dtos;
    }

    // 유저 로그인
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();
    }

    // 유저 정보 수정
    @Transactional
    public void updateUser(UserRequestDto userRequestDto, String username) {

        // JPA에서는 보통 조회한 영속 엔티티를 수정
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        // setter 대신 entity의 변경 메서드 사용
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            userEntity.changePassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));
        }

        if (userRequestDto.getNickname() != null && !userRequestDto.getNickname().isEmpty()) {
            userEntity.changeNickname(userRequestDto.getNickname());
        }

        // @Transactional 안에서 이미 조회한 엔티티를 수정하는 중이다.
        // userEntity는 영속 상태라서 트랜잭션 끝날 때 Dirty Checking으로 자동 Update 된다.
        // 아래 save는 없어도 된다.
        userRepository.save(userEntity);
    }

    // 유저 정보 삭제
    @Transactional
    public void deleteUser(String username) {

        userRepository.deleteByUsername(username);
    }
}
