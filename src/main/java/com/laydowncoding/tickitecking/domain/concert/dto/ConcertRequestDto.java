package com.laydowncoding.tickitecking.domain.concert.dto;

import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatPriceRequestDto;
import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertRequestDto {

    private String name;
    private String description;
    private LocalDateTime startTime;
    private Long auditoriumId;
    private List<SeatPriceRequestDto> seatPrices;
    private List<SeatRequestDto> seatList;
}
