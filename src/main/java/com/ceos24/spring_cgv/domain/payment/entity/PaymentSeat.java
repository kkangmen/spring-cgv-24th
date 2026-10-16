package com.ceos24.spring_cgv.domain.payment.entity;

import com.ceos24.spring_cgv.domain.reservation.enums.AgeGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", length = 10, nullable = false)
    private AgeGroup ageGroup;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Builder
    private PaymentSeat(Payment payment, Integer seatRow, Integer seatNumber, AgeGroup ageGroup, Integer price) {
        this.payment = payment;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.ageGroup = ageGroup;
        this.price = price;
    }
}
