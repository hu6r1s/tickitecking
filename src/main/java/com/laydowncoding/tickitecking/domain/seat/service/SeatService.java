package com.laydowncoding.tickitecking.domain.seat.service;

import com.laydowncoding.tickitecking.domain.seat.dto.AuditoriumCapacityDto;
import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatPriceRequestDto;
import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatRequestDto;
import com.laydowncoding.tickitecking.domain.seat.dto.response.SeatPriceResponseDto;
import java.util.List;

public interface SeatService {

    void createSeats(List<SeatRequestDto> seatRequestDtos, Long concertId, AuditoriumCapacityDto capacityDto);

    void updateSeats(List<SeatRequestDto> seatRequestDtos, Long concertId, AuditoriumCapacityDto capacityDto);

    void deleteSeats(Long concertId);

    void createSeatPrices(Long concertId, List<SeatPriceRequestDto> seatPriceRequestDtos);

    List<SeatPriceResponseDto> updateSeatPrices(Long concertId, List<SeatPriceRequestDto> seatPriceRequestDtos);

    List<SeatPriceResponseDto> getSeatPrices(Long concertId);

    void deleteSeatPrices(Long concertId);
}
