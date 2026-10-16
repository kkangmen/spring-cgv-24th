package com.ceos24.spring_cgv.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentConfig {

    // 토스페이먼츠에 결제 승인 요청에 보낼 URL
    public static final String URL = "https://api.tosspayments.com/v1/payments/";

    private final String testClientApiKey;

    private final String testSecretKey;

    private final String successUrl;

    private final String failureUrl;

    public TossPaymentConfig(
            @Value("${payment.toss.test_client_api_key}") String testClientApiKey,
            @Value("${payment.toss.test_secret_api_key}") String testSecretKey,
            @Value("${payment.toss.success_url}") String successUrl,
            @Value("${payment.toss.failure_url}") String failureUrl) {
        this.testClientApiKey = testClientApiKey;
        this.testSecretKey = testSecretKey;
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
    }
}
