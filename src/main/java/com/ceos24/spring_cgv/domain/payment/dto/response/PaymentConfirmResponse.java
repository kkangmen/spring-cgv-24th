package com.ceos24.spring_cgv.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "결제 승인 응답")
public record PaymentConfirmResponse() {
}
