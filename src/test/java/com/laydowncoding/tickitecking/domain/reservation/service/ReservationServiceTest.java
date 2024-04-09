package com.laydowncoding.tickitecking.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;

import com.laydowncoding.tickitecking.domain.concert.repository.ConcertRepository;
import com.laydowncoding.tickitecking.domain.reservations.dto.ConcertInfoDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ConcertSeatResponseDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ReservationRequestDto;
import com.laydowncoding.tickitecking.domain.reservations.dto.ReservationResponseDto;
import com.laydowncoding.tickitecking.domain.reservations.entity.Reservation;
import com.laydowncoding.tickitecking.domain.reservations.entity.UnreservableSeat;
import com.laydowncoding.tickitecking.domain.reservations.repository.ReservationRepository;
import com.laydowncoding.tickitecking.domain.reservations.service.ReservationServiceImpl;
import com.laydowncoding.tickitecking.domain.seat.entity.Seat;
import com.laydowncoding.tickitecking.domain.seat.repository.SeatRepository;
import com.laydowncoding.tickitecking.global.exception.CustomRuntimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationServiceImpl reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    SeatRepository seatRepository;

    @Mock
    ConcertRepository concertRepository;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    Reservation reservation;
    ReservationRequestDto reservationRequestDto;
    UnreservableSeat unreservableSeat1;
    UnreservableSeat unreservableSeat2;
    ConcertInfoDto concertInfoDto;
    Seat seat;

    @BeforeEach
    void setup() {
        reservation = Reservation.builder()
            .status("Y")
            .userId(1L)
            .concertId(1L)
            .seatId(1L)
            .build();
        reservationRequestDto = ReservationRequestDto.builder()
            .horizontal("A")
            .vertical("1")
            .build();
        unreservableSeat1 = UnreservableSeat.builder()
            .horizontal("A")
            .vertical("2")
            .isLocked(false)
            .isReserved(true)
            .build();
        unreservableSeat2 = UnreservableSeat.builder()
            .horizontal("A")
            .vertical("3")
            .isLocked(true)
            .isReserved(false)
            .build();
        concertInfoDto = ConcertInfoDto.builder()
            .concertId(1L)
            .concertName("ConcertName")
            .concertDescription("ConcertDescription")
            .concertStartTime(LocalDateTime.now())
            .concertGoldPrice(100.0)
            .concertSilverPrice(75.0)
            .concertBronzePrice(50.0)
            .auditoriumId(1L)
            .auditoriumName("AuditoriumName")
            .auditoriumAddress("강남구")
            .auditoriumMaxColumn("Z")
            .auditoriumMaxRow("10")
            .build();
        seat = Seat.builder()
            .concertId(1L)
            .horizontal("A")
            .vertical("1")
            .reserved("N")
            .grade("G")
            .availability("Y")
            .auditoriumId(1L)
            .build();
    }

    @DisplayName("예매 생성 - 성공")
    @Test
    void reservations_create_success() {
        //given
        given(seatRepository.findSeatForReservation(any(), any(), any()))
            .willReturn(seat);
        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().setIfAbsent(any(), any())).willReturn(true);

        //when
        ReservationResponseDto responseDto = reservationService.createReservation(1L,
            1L, reservationRequestDto);

        //then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getStatus()).isEqualTo("Y");
    }

    @DisplayName("예매 생성 - 실패 예약 불가능한 좌석")
    @Test
    void reservation_create_fail() {
        //given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().setIfAbsent(any(), any())).willReturn(false);

        //when & then
        assertThatThrownBy(() ->
            reservationService.createReservation(1L, 1L, reservationRequestDto))
            .isInstanceOf(CustomRuntimeException.class);
    }

    @DisplayName("콘서트 좌석 조회- 성공")
    @Test
    void concert_seats_get_success() {
        //given
        given(reservationRepository.findUnreservableSeats(any()))
            .willReturn(List.of(unreservableSeat1, unreservableSeat2));
        given(reservationRepository.findConcertInfo(any()))
            .willReturn(concertInfoDto);
        given(concertRepository.existsById(any())).willReturn(true);

        //when
        ConcertSeatResponseDto response = reservationService.getConcertSeats(1L);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getUnreservableSeats()).hasSize(2);
    }

    @DisplayName("콘서트 좌석 조회- 실패 없는 콘서트 id")
    @Test
    void concert_seats_get_fail() {
        //given
        given(concertRepository.existsById(any())).willReturn(false);

        //when & then
        assertThatThrownBy(() ->
            reservationService.getConcertSeats(1L))
            .isInstanceOf(CustomRuntimeException.class);
    }

    @DisplayName("예매 삭제 - 성공")
    @Test
    void delete_reservation_success() {
        //given
        given(reservationRepository.findById(any())).willReturn(Optional.of(reservation));
        given(seatRepository.findById(anyLong())).willReturn(Optional.of(seat));

        //when & then
        assertDoesNotThrow(() -> reservationService.deleteReservation(1L, 1L));
        verify(reservationRepository, times(1)).delete(any(Reservation.class));
    }

    @DisplayName("예매 삭제 - 실패 없는 예매 id")
    @Test
    void delete_reservation_fail() {
        //given
        given(reservationRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reservationService.deleteReservation(1L, 1L));
    }
}
