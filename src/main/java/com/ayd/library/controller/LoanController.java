package com.ayd.library.controller;


import com.ayd.library.dto.LoanRequestDto;
import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.exception.*;
import com.ayd.library.model.Loan;
import com.ayd.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRequestDto loanRequestDto) throws ServiceException {

        return ResponseEntity.ok(loanService.createLoan(loanRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody Loan loan) throws NotFoundException {
        return ResponseEntity.ok(loanService.updateLoan(id, loan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Loan> closeLoan(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(loanService.closeLoan(id));
    }
}