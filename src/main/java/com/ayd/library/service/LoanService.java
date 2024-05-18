package com.ayd.library.service;

import com.ayd.library.dto.LoanRequestDto;
import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.enums.LoanStatus;
import com.ayd.library.exception.*;
import com.ayd.library.model.Loan;
import com.ayd.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    final LoanRepository repository;
    final StudentService studentService;
    final BookService bookService;

    @Transactional
    public Loan createLoan(LoanRequestDto loan) throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
        if (loan.getTotalDue().compareTo(BigDecimal.ZERO) < 0)
            throw new QuantityException("Cannot create loan without total due: " + loan.getTotalDue());

        if (loan.getBookCode() == null) {
            throw new RequiredEntityException("Book code must not be null");
        }

        var studentEntity = studentService.getStudentByCarnet(loan.getCarnet());
        var bookEntity = bookService.getBookByCode(loan.getBookCode());

        if (bookEntity.getAvailableCopies() == 0) {
            throw new EnoughException("Enough available copies");
        }

        if (repository.findById(loan.getId()).isPresent()) {
            throw new DuplicatedEntityException("Loan with ID already exists: " + loan.getId());
        }

        Loan entity = Loan.builder()
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .bookCode(bookEntity)
                .student(studentEntity)
                .totalDue(loan.getTotalDue())
                .status(LoanStatus.ACTIVE.name())
                .build();

        return repository.save(entity);
    }

    @Transactional
    public Loan updateLoan(Long id, Loan updatedLoan) throws NotFoundException {
        return repository.findById(id)
                .map(existingLoan -> {
                    //existingLoan.setBook(updatedLoan.getBook());
                    existingLoan.setStudent(updatedLoan.getStudent());
                    existingLoan.setLoanDate(updatedLoan.getLoanDate());
                    existingLoan.setReturnDate(updatedLoan.getReturnDate());
                    existingLoan.setStatus(updatedLoan.getStatus());
                    return repository.save(existingLoan);
                })
                .orElseThrow(() -> new NotFoundException("Loan not found with ID: " + id));
    }

    public Loan getLoanById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found with ID: " + id));
    }

    public List<LoanResponseDto> getAllLoans() {
        return repository.findLoanDetails();
    }
    @Transactional
    public Loan closeLoan(Long id) throws NotFoundException {
        Loan loan = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found with ID: " + id));
        loan.setStatus("returned");
        return repository.save(loan);
    }
    public List<Loan> findLoansByStatus(String status) {
        return repository.findAllByStatus(status);
    }


}
