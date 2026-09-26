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
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401_3" , "만료된 토큰입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "AUTH401_4","토큰 검증 중 오류가 발생했습니다." ),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401_5" , "유효하지 않은 토큰입니다."),
    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED,"AUTH401_6" ,"토큰의 타입 정보가 일치하지 않습니다." ),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409_1", "이미 가입된 이메일입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}