package com.ayd.library.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanResponseDto {
    Long id;
    String name;
    LocalDate loanDate;
    LocalDate returnDate;
    String status;
    BigDecimal totalDue;

    public LoanResponseDto(Long id, String name, LocalDate loanDate, LocalDate returnDate, String status, BigDecimal totalDue) {
        this.id = id;
        this.name = name;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.status = status;
        this.totalDue = totalDue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
