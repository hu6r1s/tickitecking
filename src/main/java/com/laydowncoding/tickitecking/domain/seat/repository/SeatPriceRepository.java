package com.laydowncoding.tickitecking.domain.seat.repository;

import com.laydowncoding.tickitecking.domain.seat.entity.SeatPrice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {

    List<SeatPrice> findAllByConcertId(Long concertId);

    @Modifying(clearAutomatically = true)
    @Query("delete from SeatPrice sp where sp.concertId = :concertId")
    void deleteAllByConcertId(Long concertId);
}
