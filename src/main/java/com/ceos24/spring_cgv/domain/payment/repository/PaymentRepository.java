package com.ceos24.spring_cgv.domain.payment.repository;

import com.ceos24.spring_cgv.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 함수 기능: 주문 번호로 결제를 조회한다. 결제 승인 시 금액 대조에 사용한다.
     * @param orderId 서버가 생성한 주문 번호
     * @return 해당 주문 번호의 결제
     */
    Optional<Payment> findByOrderId(String orderId);
}
