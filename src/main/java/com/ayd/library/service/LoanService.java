package com.ayd.library.service;

import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Loan;
import com.ayd.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository repository;

    @Transactional
    public Loan createLoan(Loan loan) throws DuplicatedEntityException {
        if (repository.findById(loan.getId()).isPresent()) {
            throw new DuplicatedEntityException("Loan with ID already exists: " + loan.getId());
        }
        loan.setStatus("active");
        return repository.save(loan);
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
