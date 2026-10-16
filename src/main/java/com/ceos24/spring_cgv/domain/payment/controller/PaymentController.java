package com.ceos24.spring_cgv.domain.payment.controller;

import com.ceos24.spring_cgv.domain.payment.dto.request.PaymentReadyRequest;
import com.ceos24.spring_cgv.domain.payment.dto.response.PaymentReadyResponse;
import com.ceos24.spring_cgv.domain.payment.exception.code.PaymentSuccessCode;
import com.ceos24.spring_cgv.domain.payment.service.PaymentService;
import com.ceos24.spring_cgv.global.apipayload.ApiResponse;
import com.ceos24.spring_cgv.global.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제", description = "토스페이먼츠 결제 관련 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 요청 정보 사전 등록",
            description = "선택한 좌석의 재고를 검증하고 선점한 뒤, 토스 결제창에 필요한 주문 번호와 결제 금액을 반환합니다.")
    @PostMapping("/ready")
    public ApiResponse<PaymentReadyResponse> ready(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentReadyRequest request
    ) {

        return ApiResponse.onSuccess(
                PaymentSuccessCode.PAYMENT_READY,
                paymentService.ready(userDetails.getMemberId(), request));
    }
}
