package com.laydowncoding.tickitecking.domain.reservation.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.laydowncoding.tickitecking.domain.reservations.dto.ReservationRequestDto;
import com.laydowncoding.tickitecking.domain.reservations.repository.ReservationRepository;
import com.laydowncoding.tickitecking.domain.reservations.service.ReservationService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@Rollback
@ActiveProfiles("test")
public class ReservationIntegrationTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @DisplayName("동시에 한자리 예매시 첫번째 요청만 예매성공한다.")
    @Test
    @Sql(scripts = "/reservation-test-data.sql",
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED))
    void concurrency_test() throws InterruptedException {
        //given
        int tryCount = 100;
        long userId = 1L;
        Long concertId = 1L;
        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
            .horizontal("A")
            .vertical("0")
            .build();
        ExecutorService executor = Executors.newFixedThreadPool(16);

        //when
        CountDownLatch latch = new CountDownLatch(tryCount);
        for (int i = 0; i < tryCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    reservationService.createReservation(userId + finalI, concertId,
                        reservationRequestDto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //then
        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @AfterEach
    void clearRedis() {
        redisTemplate.delete("1A0");
    }
}
