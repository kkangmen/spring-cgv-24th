package com.ceos24.spring_cgv.domain.payment.exception.code;

import com.ceos24.spring_cgv.global.apipayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentSuccessCode implements BaseSuccessCode {

    PAYMENT_READY(HttpStatus.OK, "PAYMENT200_1", "결제 요청 정보가 성공적으로 등록되었습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
