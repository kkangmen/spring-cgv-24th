package com.ceos24.spring_cgv.domain.auth.exception.code;

import com.ceos24.spring_cgv.global.apipayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH401_2", "인증 정보가 없거나 유효하지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409_1", "이미 가입된 이메일입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}