package com.laydowncoding.tickitecking.domain.reservations.service;

import static com.laydowncoding.tickitecking.global.exception.errorcode.ReservationErrorCode.INVALID_USER_ID;
import static com.laydowncoding.tickitecking.global.exception.errorcode.ReservationErrorCode.NOT_FOUND_RESERVATION;

import com.laydowncoding.tickitecking.domain.concert.repository.ConcertRepository;
import com.laydowncoding.tickitecking.domain.reservations.dto.ConcertInfoDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ConcertSeatResponseDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ReservationRequestDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ReservationResponseDto;
import com.laydowncoding.tickitecking.domain.reservations.entity.Reservation;
import com.laydowncoding.tickitecking.domain.reservations.entity.UnreservableSeat;
import com.laydowncoding.tickitecking.domain.reservations.repository.ReservationRepository;
import com.laydowncoding.tickitecking.domain.seat.entity.Seat;
import com.laydowncoding.tickitecking.domain.seat.repository.SeatRepository;
import com.laydowncoding.tickitecking.global.exception.CustomRuntimeException;
import com.laydowncoding.tickitecking.global.exception.errorcode.ConcertErrorCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ReservationResponseDto createReservation(Long userId, Long concertId,
        ReservationRequestDto requestDto) {

        if (isTaken(concertId, requestDto.getHorizontal(), requestDto.getVertical())) {
            throw new CustomRuntimeException("이미 예약된 좌석입니다.");
        }

        Seat seat = seatRepository.findSeatForReservation(concertId, requestDto.getHorizontal(),
            requestDto.getVertical());
        if (!seat.isReservable()) {
            throw new CustomRuntimeException("예약 불가능한 좌석입니다.");
        }
        seat.reserve();

        Reservation reservation = Reservation.builder()
            .status("Y")
            .userId(userId)
            .concertId(concertId)
            .seatId(seat.getId())
            .build();
        Reservation save = reservationRepository.save(reservation);

        return ReservationResponseDto.builder()
            .id(save.getId())
            .status(save.getStatus())
            .userId(save.getUserId())
            .concertId(save.getConcertId())
            .seatId(save.getSeatId())
            .build();
    }

    @Override
    public ConcertSeatResponseDto getConcertSeats(Long concertId) {
        validateConcertId(concertId);
        List<UnreservableSeat> unreservableSeats = reservationRepository.findUnreservableSeats(
            concertId);
        ConcertInfoDto concertInfoDto = reservationRepository.findConcertInfo(concertId);
        return ConcertSeatResponseDto.builder()
            .concertInfoDto(concertInfoDto)
            .unreservableSeats(unreservableSeats)
            .build();
    }

    @Override
    public void deleteReservation(Long userId, Long reservationId) {
        Reservation reservation = findReservation(reservationId);
        validateUserId(reservation.getUserId(), userId);
        Seat seat = seatRepository.findById(reservation.getSeatId()).orElseThrow();
        seat.cancel();
        reservationRepository.delete(reservation);
    }

    private Boolean isTaken(Long concertId, String horizontal, String vertical) {
        String key = concertId + horizontal + vertical;
        return Boolean.FALSE.equals(
            redisTemplate.opsForValue().setIfAbsent(key, "reserved"));
    }

    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(
            () -> new CustomRuntimeException(NOT_FOUND_RESERVATION.getMessage())
        );
    }

    private void validateUserId(Long origin, Long input) {
        if (!Objects.equals(origin, input)) {
            throw new CustomRuntimeException(INVALID_USER_ID.getMessage());
        }
    }

    private void validateConcertId(Long concertId) {
        if (!concertRepository.existsById(concertId)) {
            throw new CustomRuntimeException(ConcertErrorCode.NOT_FOUND_CONCERT.getMessage());
        }
    }
}
