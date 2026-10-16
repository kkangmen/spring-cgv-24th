package com.ceos24.spring_cgv.domain.payment.service;

import com.ceos24.spring_cgv.domain.member.entity.Member;
import com.ceos24.spring_cgv.domain.member.exception.MemberException;
import com.ceos24.spring_cgv.domain.member.exception.code.MemberErrorCode;
import com.ceos24.spring_cgv.domain.member.repository.MemberRepository;
import com.ceos24.spring_cgv.domain.movie.entity.ScreenType;
import com.ceos24.spring_cgv.domain.movie.entity.Screening;
import com.ceos24.spring_cgv.domain.movie.repository.ScreeningRepository;
import com.ceos24.spring_cgv.domain.payment.dto.SeatPosition;
import com.ceos24.spring_cgv.domain.payment.dto.request.PaymentReadyRequest;
import com.ceos24.spring_cgv.domain.payment.dto.response.PaymentReadyResponse;
import com.ceos24.spring_cgv.domain.payment.entity.Payment;
import com.ceos24.spring_cgv.domain.payment.entity.PaymentSeat;
import com.ceos24.spring_cgv.domain.payment.exception.PaymentException;
import com.ceos24.spring_cgv.domain.payment.exception.code.PaymentErrorCode;
import com.ceos24.spring_cgv.domain.payment.repository.PaymentRepository;
import com.ceos24.spring_cgv.domain.payment.repository.PaymentSeatRepository;
import com.ceos24.spring_cgv.domain.reservation.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentSeatRepository paymentSeatRepository;
    // 동시성 문제를 유발시키기 위해 주석 처리 (되살릴 때 SeatHoldRepository import도 함께 추가해야 한다)
//    private final SeatHoldRepository seatHoldRepository;
    private final MemberRepository memberRepository;
    private final ScreeningRepository screeningRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 주문 번호 접두어.
    private static final String ORDER_ID_PREFIX = "cgv_";

    /***
     * 함수 기능: 결제 요청 정보를 사전 등록한다. 좌석 재고를 검증하고 주문 번호를 생성하여
     *          토스 결제창에 필요한 정보를 반환한다.
     *          동시성 제어가 없는 상태이므로 같은 좌석에 대한 동시 요청이 모두 통과한다.
     * @param memberId 결제를 요청한 회원 식별자
     * @param request 상영회차 ID와 선택한 좌석 목록
     * @return PaymentReadyResponse 주문 번호, 결제 금액, 주문명, 구매자 이름
     */
    @Transactional
    public PaymentReadyResponse ready(Long memberId, PaymentReadyRequest request) {

        Member member = getMember(memberId);

        // 금액 계산과 좌석 검증에 필요한 연관(영화, 상영관 종류)을 함께 가져온다.
        Screening screening = findScreeningById(request.screeningId());

        // 이미 시작한 회차는 예매할 수 없다.
        if (screening.getStartTime().isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_SCREENING_CLOSED);
        }

        // SeatPosition(내부 DTO) 리스트를 생성한다. -> 매번 request.seats()를 통해 꺼내오면 귀찮아서.
        List<SeatPosition> seatPositions = request.seats().stream()
                .map(seat -> new SeatPosition(seat.seatRow(), seat.seatNumber()))
                .toList();

        // 요청받은 좌석이 해당 상영관에 유효한지 검증한다. (GENERAL, SPECIAL)
        validateSeatPositions(seatPositions, screening.getScreen().getScreenType());

        // 요청받은 좌석이 이미 예매가 된 좌석이 아님을 검증한다.
        validateSeatsNotReserved(seatPositions, screening.getId());

        // orderId (토스페이먼츠에 전달한 주문번호 생성)
        String orderId = ORDER_ID_PREFIX + UUID.randomUUID().toString().substring(0, 8);

        // 결제가 끝날 때까지 좌석을 잡아둔다. 실패하면 다른 고객이 결제 중인 좌석이다.
        // 동시성 문제를 유발시키기 위해 주석 처리
//        if (!seatHoldRepository.hold(screening.getId(), seatPositions, orderId)) {
//            throw new PaymentException(PaymentErrorCode.PAYMENT_SEAT_ALREADY_HELD);
//        }

        // 좌석 1석의 기본 요금 (상영 회차에 대한 요금 + 상영관에 대한 요금)
        int seatBasePrice = calculateSeatBasePrice(screening);

        // Payment 객체를 DB에 저장한다. (추후 토스페이먼츠 결제 승인을 보내고 받은 응답으로 orderId, orderName, amount를 검증하기 위해)
        Payment payment = paymentRepository.save(Payment.builder()
                .member(member)
                .screening(screening)
                .orderId(orderId)
                .orderName(screening.getMovie().getTitle() + " " + request.seats().size() + "석") // ex) 오디세우스 2석
                .totalAmount(calculateTotalAmount(request.seats(), seatBasePrice))
                .build());

        // 결제 시도 시점의 좌석/연령/금액을 스냅샷으로 남긴다. 결제 승인 후 예매 확정에 사용한다.
        paymentSeatRepository.saveAll(request.seats().stream()
                .map(seat -> PaymentSeat.builder()
                        .payment(payment)
                        .seatRow(seat.seatRow())
                        .seatNumber(seat.seatNumber())
                        .ageGroup(seat.ageGroup())
                        .price(seatBasePrice + seat.ageGroup().getAdjustment())
                        .build())
                .toList());

        log.info("[결제 요청 등록] memberId={}, orderId={}, amount={}",
                memberId, orderId, payment.getTotalAmount());

        return PaymentReadyResponse.from(payment);
    }

    /***
     * 함수 기능: 같은 좌석을 중복 선택했는지, 상영관에 존재하는 좌석인지 검사한다.
     * @param seats 요청한 좌석 좌표 목록
     * @param screenType 좌석 배치(행/열 개수)를 가진 상영관 종류
     */
    private void validateSeatPositions(List<SeatPosition> seats, ScreenType screenType) {

        // 하나의 영화 예매 요청에 중복된 좌석이 존재한다면, 에러를 반환한다.
        if (Set.copyOf(seats).size() != seats.size()) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_SEAT_DUPLICATED);
        }

        // 좌석은 rowCount x colCount 직사각형으로 존재한다.
        boolean outOfRange = seats.stream().anyMatch(seat ->
                seat.seatRow() > screenType.getRowCount() || seat.seatNumber() > screenType.getColCount());

        if (outOfRange) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_SEAT_OUT_OF_RANGE);
        }
    }

    /***
     * 함수 기능: 이미 예매가 확정된 좌석이 포함되어 있는지 검사한다.
     * @param seats 요청한 좌석 좌표 목록
     * @param screeningId 상영회차 식별자
     */
    private void validateSeatsNotReserved(List<SeatPosition> seats, Long screeningId) {

        // 이미 예매된 상영 시간의 좌석을 Set에 담는다.
        Set<SeatPosition> reserved = reservationSeatRepository.findAllByScreeningId(screeningId).stream()
                .map(seat -> new SeatPosition(seat.getSeatRow(), seat.getSeatNumber()))
                .collect(Collectors.toSet());

        // 만약, 요청한 좌석 좌표 목록이 reserved 목록에 있다면 -> 예외를 반환한다.
        if (seats.stream().anyMatch(reserved::contains)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_SEAT_ALREADY_TAKEN);
        }
    }

    /***
     * 함수 기능: 연령 조정액을 적용하기 전, 좌석 1석의 기본 금액을 계산한다.
     * @param screening 상영회차
     * @return 상영회차 기본가 + 상영관 종류 추가 요금
     */
    private int calculateSeatBasePrice(Screening screening) {
        return screening.getBasePrice() + screening.getScreen().getScreenType().getExtraCharge();
    }

    /***
     * 함수 기능: 좌석별 연령 조정액을 반영한 결제 총 금액을 계산한다.
     * @param seats 요청한 좌석 목록
     * @param seatBasePrice 좌석 1석의 기본 금액
     * @return 결제 총 금액
     */
    private int calculateTotalAmount(List<PaymentReadyRequest.SeatRequest> seats, int seatBasePrice) {
        return seats.stream()
                .mapToInt(seat -> seatBasePrice + seat.ageGroup().getAdjustment())
                .sum();
    }

    private Screening findScreeningById(Long screeningId) {
        return screeningRepository.findWithDetailsById(screeningId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_SCREENING_NOT_FOUND));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
