package com.laydowncoding.tickitecking.domain.seat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String vertical;

  @Column
  private String horizontal;

  @Column(columnDefinition = "CHAR(1) default 'Y'")
  private String availability;

  @Column(columnDefinition = "CHAR(1)")
  private String grade;

  @Column(nullable = false)
  private Long auditoriumId;

  @Column(nullable = false)
  private Long concertId;

  @Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
  private String reserved;

  public Seat(String vertical, String horizontal, String availability, String grade, Long auditoriumId) {
    this.vertical = vertical;
    this.horizontal = horizontal;
    this.availability = availability;
    this.grade = grade;
    this.auditoriumId = auditoriumId;
  }

  @Builder
  public Seat(String vertical, String horizontal, String grade,
      Long auditoriumId, Long concertId, String reserved, String availability) {
    this.vertical = vertical;
    this.horizontal = horizontal;
    this.grade = grade;
    this.auditoriumId = auditoriumId;
    this.concertId = concertId;
    this.reserved = reserved;
    this.availability = availability;
  }

  public void update(String grade) {
    this.grade = grade;
  }

  public void togleLock() {
    if ("Y".equals(availability)) {
      this.availability = "N";
    } else {
      this.availability = "Y";
    }
  }

  public void reserve() {
    this.reserved = "Y";
  }

  public void cancel() {
    this.reserved = "N";
  }

  public boolean isReservable() {
    return this.reserved.equals("N") && this.availability.equals("Y");
  }
}
