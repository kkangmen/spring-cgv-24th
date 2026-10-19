package com.ceos24.spring_cgv.domain.payment.client;

import com.ceos24.spring_cgv.domain.payment.client.dto.TossPaymentConfirmRequest;
import com.ceos24.spring_cgv.domain.payment.client.dto.TossPaymentConfirmResponse;
import com.ceos24.spring_cgv.domain.payment.client.dto.TossPaymentErrorResponse;
import com.ceos24.spring_cgv.domain.payment.exception.PaymentException;
import com.ceos24.spring_cgv.domain.payment.exception.code.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentClient {

    private final RestClient tossRestClient;

    /***
     * 함수 기능: 토스페이먼츠에 결제 승인을 요청한다.
     * @param paymentKey 토스가 발급한 결제 키
     * @param orderId 서버가 생성한 주문 번호
     * @param totalAmount 승인할 금액 (DB에 저장된 값이어야 한다)
     * @return 승인된 결제 정보
     */
    public TossPaymentConfirmResponse confirm(String paymentKey, String orderId, Integer totalAmount) {

        try {
            return tossRestClient.post()
                    .uri("/confirm")
                    .body(TossPaymentConfirmRequest.of(paymentKey, orderId, totalAmount))
                    .retrieve()
                    .body(TossPaymentConfirmResponse.class);

        } catch (RestClientResponseException e) {
            logFailure(orderId, e);
            throw new PaymentException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (ResourceAccessException e) {
            log.error("[결제 승인 응답 없음] orderId={}, 승인 여부를 확인할 수 없어 결제 내역 대조가 필요합니다.", orderId, e);
            throw new PaymentException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    /***
     * 함수 기능: 토스가 내려준 실패 사유를 로그로 남긴다.
     * @param orderId 승인을 시도한 주문 번호
     * @param response 토스의 에러 응답
     */
    private void logFailure(String orderId, RestClientResponseException response) {

        try {
            TossPaymentErrorResponse error = response.getResponseBodyAs(TossPaymentErrorResponse.class);

            log.error("[결제 승인 실패] orderId={}, httpStatus={}, code={}, message={}",
                    orderId, response.getStatusCode(), error.code(), error.message());

        } catch (Exception e) {
            // 게이트웨이 장애 등으로 본문이 JSON이 아닐 수 있다. 이때도 승인 실패 자체는 기록해야 한다.
            log.error("[결제 승인 실패] orderId={}, 토스 에러 응답을 해석할 수 없습니다.", orderId, e);
        }
    }
}