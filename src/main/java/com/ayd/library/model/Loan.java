package com.ayd.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan")
@Getter  @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "loanDate", nullable = false)
    private LocalDate loanDate;

    @Column(name = "returnDate")
    private LocalDate returnDate;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "totalDue", precision = 10, scale = 2)
    private BigDecimal totalDue;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookCode", nullable = false)
    private Book bookCode;

}