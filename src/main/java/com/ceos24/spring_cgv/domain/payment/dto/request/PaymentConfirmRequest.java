package com.ceos24.spring_cgv.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 승인 요청")
public record PaymentConfirmRequest() {
}
