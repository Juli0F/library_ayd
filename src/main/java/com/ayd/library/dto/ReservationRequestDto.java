package com.ayd.library.dto;

import java.time.LocalDate;

public class ReservationRequestDto {
    String carnet;
    String bookCode;
    LocalDate reservationDate;
    String status;
    Long id;

    public ReservationRequestDto(String carnet, String bookCode, LocalDate reservationDate, String status, Long id) {
        this.carnet = carnet;
        this.bookCode = bookCode;
        this.reservationDate = reservationDate;
        this.status = status;
        this.id = id;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
