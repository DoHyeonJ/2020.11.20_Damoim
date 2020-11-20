package com.hiclub.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id; //고유 id

    @Column(unique = true)
    private String nickname; //닉네임

    private String password; //비밀번호

    @Column(unique = true) //이메일
    private String email;

    private boolean emailVerified; //이메일인증여부

    private String emailCheckToken; //이메일인증토큰

    private LocalDateTime emailCheckTokenGeneratedAt; //이메일인증시간

    private LocalDateTime joinedAt; //가입일

    private String bio; //프로필소개

    private String url; //프로필 url

    private String occupation; //프로필직업

    private String location; //프로필주소

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage; //프로필이미지

    private boolean clubCreateByEmail; //모임생성알림 수신여부(이메일)

    private boolean clubCreateByWeb; //모임생성알림 수신여부(웹)

    private boolean clubEnrollmentResultByEmail; //모임가입신청여부 알림

    private boolean clubEnrollmentResultByWeb; //모임가입신청여부 알림

    private boolean clubUpdateByEmail; //모임수정사항 알림

    private boolean clubUpdateByWeb; //모임수정사항 알림

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }


    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
