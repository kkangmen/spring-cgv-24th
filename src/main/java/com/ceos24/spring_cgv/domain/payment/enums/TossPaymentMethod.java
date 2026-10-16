package com.ceos24.spring_cgv.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TossPaymentMethod {

    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAY("간편결제"),
    TRANSFER("계좌이체"),
    MOBILE_PHONE("휴대폰"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    BOOK_GIFT_CERTIFICATE("도서문화상품권"),
    GAME_GIFT_CERTIFICATE("게임문화상품권");

    // 토스페이먼츠 결제 승인 응답의 method 필드 값 (한글 문자열로 내려온다)
    private final String description;
}
