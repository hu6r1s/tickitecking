package com.laydowncoding.tickitecking.domain.seat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatPriceRequestDto {

    private String grade;
    private Double price;
}
