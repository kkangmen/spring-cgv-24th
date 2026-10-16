package com.ceos24.spring_cgv.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentConfirmResult() {
}
