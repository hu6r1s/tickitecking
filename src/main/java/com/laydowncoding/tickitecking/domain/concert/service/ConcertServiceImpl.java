package com.laydowncoding.tickitecking.domain.concert.service;

import static com.laydowncoding.tickitecking.global.exception.errorcode.ConcertErrorCode.*;

import com.laydowncoding.tickitecking.domain.auditorium.repository.AuditoriumRepository;
import com.laydowncoding.tickitecking.domain.concert.dto.ConcertCreateRequestDto;
import com.laydowncoding.tickitecking.domain.concert.dto.ConcertResponseDto;
import com.laydowncoding.tickitecking.domain.concert.dto.ConcertUpdateRequestDto;
import com.laydowncoding.tickitecking.domain.concert.entitiy.Concert;
import com.laydowncoding.tickitecking.domain.concert.repository.ConcertRepository;
import com.laydowncoding.tickitecking.global.exception.CustomRuntimeException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final AuditoriumRepository auditoriumRepository;

    @Override
    public void createConcert(Long companyUserId, ConcertCreateRequestDto requestDto) {
        validateAuditoriumId(requestDto.getAuditoriumId());

        Concert concert = Concert.builder()
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .startTime(requestDto.getStartTime())
            .companyUserId(companyUserId)
            .auditoriumId(requestDto.getAuditoriumId())
            .build();
        concertRepository.save(concert);
    }

    @Override
    public ConcertResponseDto getConcert(Long concertId) {
        Concert concert = findConcert(concertId);
        return ConcertResponseDto.builder()
            .id(concert.getId())
            .name(concert.getName())
            .description(concert.getDescription())
            .startTime(concert.getStartTime())
            .companyUserId(concert.getCompanyUserId())
            .auditoriumId(concert.getAuditoriumId())
            .build();
    }

    @Override
    public List<ConcertResponseDto> getAllConcerts() {
        List<Concert> concerts = concertRepository.findAll();
        return concerts.stream()
            .map(concert ->
                ConcertResponseDto.builder()
                    .id(concert.getId())
                    .name(concert.getName())
                    .description(concert.getDescription())
                    .startTime(concert.getStartTime())
                    .companyUserId(concert.getCompanyUserId())
                    .auditoriumId(concert.getAuditoriumId())
                    .build()
            )
            .toList();
    }

    @Override
    public void updateConcert(Long companyUserId, Long concertId,
        ConcertUpdateRequestDto requestDto) {
        Concert concert = findConcert(concertId);
        validateCompanyUserId(concert.getCompanyUserId(), companyUserId);

        concert.update(requestDto);
    }

    @Override
    public void deleteConcert(Long companyUserId, Long concertId) {
        Concert concert = findConcert(concertId);
        validateCompanyUserId(concert.getCompanyUserId(), companyUserId);

        concertRepository.delete(concert);
    }

    private void validateCompanyUserId(Long origin, Long input) {
        if (!Objects.equals(origin, input)) {
            throw new CustomRuntimeException(INVALID_COMPANY_USER_ID.getMessage());
        }
    }

    private Concert findConcert(Long concertId) {
        return concertRepository.findById(concertId).orElseThrow(
            () -> new CustomRuntimeException(NOT_FOUND_CONCERT.getMessage())
        );
    }

    private void validateAuditoriumId(Long auditoriumId) {
        auditoriumRepository.findById(auditoriumId).orElseThrow(
            () -> new CustomRuntimeException(NOT_FOUND_AUDITORIUM.getMessage())
        );
    }
}