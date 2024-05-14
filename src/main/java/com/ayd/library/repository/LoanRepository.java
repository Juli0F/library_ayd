package com.ayd.library.repository;

import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByStatus(String status);

    @Query("SELECT new com.ayd.library.dto.LoanResponseDto(l.id, s.carnet,b.title, l.loanDate, l.returnDate, l.status, l.totalDue) " +
            "FROM Loan l JOIN l.student s JOIN l.bookCode b")
    List<LoanResponseDto> findLoanDetails();
}