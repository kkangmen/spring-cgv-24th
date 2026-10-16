package com.ceos24.spring_cgv.domain.reservation.repository;

import com.ceos24.spring_cgv.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    /**
     * 함수 기능: 해당 상영회차에 이미 예매된 좌석을 모두 조회한다.
     * @param screeningId 상영회차 식별자
     * @return 예매가 확정된 좌석 목록
     */
    List<ReservationSeat> findAllByScreeningId(Long screeningId);
}
