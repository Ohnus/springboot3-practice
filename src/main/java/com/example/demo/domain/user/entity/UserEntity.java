package com.example.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
// Spring Data 공통 어노테이션 => MongoDB, Redis 등에서 사용
//import org.springframework.data.annotation.Id;

// 스프링이 해당 클래스를 Entity로 인식하도록 하는 어노테이션
@Entity
// getter를 롬복이 대신 생성하도록 하는 어노테이션
@Getter
// JPA는 기본 생성자가 필요하지만, 아무데서나 new 하는 건 막고 싶으니 protected
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 테이블명 지정
@Table(name = "users")
public class UserEntity {

    // 해당 필드가 엔티티의 Primary Key임을 명시 => JPA가 이 정보를 사용해서 PK 매핑, Entity 식별, 영속성 컨텍스트 관리를 함
    @Id
    // 엔티티의 기본키(PK) 값을 어떻게 생성할지 JPA에게 알려주는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    // 해당 어노테이션이 없으면 DB는 enum을 0, 1 등으로 저장한다.
    // 상수 값 그대로 저장하기 위해 아래처럼 설정한다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role;

    // 보통 id는 db가 생성하므로, 필요한 값만 받는 생성자 생성하여 Builder를 붙여준다.
    @Builder
    public UserEntity(String username, String password, String nickname, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
