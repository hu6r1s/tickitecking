package com.laydowncoding.tickitecking.domain.concert.dto;

import com.laydowncoding.tickitecking.domain.seat.dto.response.SeatPriceResponseDto;
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
public class ConcertResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private Long companyUserId;
    private Long auditoriumId;
    private String auditoriumName;
    private String auditoriumAddress;
    private String auditoriumMaxColumn;
    private String auditoriumMaxRow;
    private List<SeatPriceResponseDto> seatPrices;
}
