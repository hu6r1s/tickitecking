package com.laydowncoding.tickitecking.domain.seat.service;

import static com.laydowncoding.tickitecking.global.exception.errorcode.SeatErrorCode.INVALID_HORIZONTAL;

import com.laydowncoding.tickitecking.domain.seat.dto.AuditoriumCapacityDto;
import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatPriceRequestDto;
import com.laydowncoding.tickitecking.domain.seat.dto.request.SeatRequestDto;
import com.laydowncoding.tickitecking.domain.seat.dto.response.SeatPriceResponseDto;
import com.laydowncoding.tickitecking.domain.seat.entity.Seat;
import com.laydowncoding.tickitecking.domain.seat.entity.SeatPrice;
import com.laydowncoding.tickitecking.domain.seat.repository.SeatPriceRepository;
import com.laydowncoding.tickitecking.domain.seat.repository.SeatRepository;
import com.laydowncoding.tickitecking.global.exception.CustomRuntimeException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatServiceImpl implements SeatService {

    private final SeatPriceRepository seatPriceRepository;
    private final SeatRepository seatRepository;

    @Override
    public void createSeats(List<SeatRequestDto> seatRequestDtos, Long concertId,
        AuditoriumCapacityDto capacityDto) {
        List<Seat> seatList = new ArrayList<>();
        char maxRowChar = capacityDto.getMaxRow().charAt(0);

        for (SeatRequestDto seatRequest : seatRequestDtos) {
            for (String horizontal : seatRequest.getHorizontals()) {
                char horizontalChar = horizontal.charAt(0);

                if (horizontalChar > maxRowChar) {
                    throw new CustomRuntimeException(INVALID_HORIZONTAL.getMessage());
                }

                for (int vertical = 1; vertical <= Integer.parseInt(capacityDto.getMaxColumn());
                    vertical++) {
                    Seat seat = Seat.builder()
                        .vertical(String.valueOf(vertical))
                        .horizontal(horizontal)
                        .grade(seatRequest.getGrade())
                        .auditoriumId(capacityDto.getAuditoriumId())
                        .concertId(concertId)
                        .reserved("N")
                        .availability("Y")
                        .build();
                    seatList.add(seat);
                }
            }
        }

        seatRepository.saveAll(seatList);
    }

    @Override
    public void updateSeats(List<SeatRequestDto> seatRequestDtos, Long concertId,
        AuditoriumCapacityDto capacityDto) {
        for (SeatRequestDto seatRequest : seatRequestDtos) {
            for (String horizontal : seatRequest.getHorizontals()) {
                for (int vertical = 1; vertical <= Integer.parseInt(capacityDto.getMaxColumn());
                    vertical++) {
                    Seat seat = seatRepository.findByConcertIdAndHorizontalAndVertical(
                        concertId, horizontal, String.valueOf(vertical));
                    if (seat != null) {
                        seat.update(seatRequest.getGrade());
                    }
                }
            }
        }
    }

    @Override
    public void deleteSeats(Long concertId) {
        List<Seat> seatList = seatRepository.findAllByConcertId(concertId);

        seatRepository.deleteAll(seatList);
    }

    @Override
    public void createSeatPrices(Long concertId, List<SeatPriceRequestDto> seatPriceRequestDtos) {
        List<SeatPrice> seatPrices = seatPricesToEntity(concertId ,seatPriceRequestDtos);
        seatPriceRepository.saveAll(seatPrices);
    }

    @Override
    public List<SeatPriceResponseDto> updateSeatPrices(Long concertId,
        List<SeatPriceRequestDto> seatPriceRequestDtos) {
        seatPriceRepository.deleteAllByConcertId(concertId);
        List<SeatPrice> seatPrices = seatPricesToEntity(concertId, seatPriceRequestDtos);
        List<SeatPrice> saved = seatPriceRepository.saveAll(seatPrices);
        return saved.stream()
            .map(seatPrice -> new SeatPriceResponseDto(seatPrice.getGrade(), seatPrice.getPrice()))
            .toList();
    }

    @Override
    public List<SeatPriceResponseDto> getSeatPrices(Long concertId) {
        List<SeatPrice> seatPrices = seatPriceRepository.findAllByConcertId(concertId);
        return seatPrices.stream()
            .map(seatPrice -> new SeatPriceResponseDto(seatPrice.getGrade(), seatPrice.getPrice()))
            .toList();
    }

    @Override
    public void deleteSeatPrices(Long concertId) {
        seatPriceRepository.deleteAllByConcertId(concertId);
    }

    private List<SeatPrice> seatPricesToEntity(Long concertId,
        List<SeatPriceRequestDto> seatPriceRequestDtos) {

        List<SeatPrice> seatPrices = new ArrayList<>();

        for (SeatPriceRequestDto requestDto : seatPriceRequestDtos) {
            SeatPrice seatPrice = SeatPrice.builder()
                .grade(requestDto.getGrade())
                .price(requestDto.getPrice())
                .concertId(concertId)
                .build();
            seatPrices.add(seatPrice);
        }
        return seatPrices;
    }
}
