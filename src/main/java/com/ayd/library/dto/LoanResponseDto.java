package com.ayd.library.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanResponseDto {
    Long id;
    String carnet;
    String bookCode;
    LocalDate loanDate;
    LocalDate returnDate;
    String status;
    BigDecimal totalDue;

    public LoanResponseDto(Long id, String carnet,String bookCode, LocalDate loanDate, LocalDate returnDate, String status, BigDecimal totalDue) {
        this.id = id;
        this.carnet = carnet;
        this.bookCode = bookCode;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.status = status;
        this.totalDue = totalDue;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }
}
