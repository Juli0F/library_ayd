package com.ayd.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "student")
@Getter  @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Student {
    @Id
    @Size(max = 10)
    @Column(name = "id", nullable = false, length = 10)
    private String carnet;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status")
    private Boolean status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "careerCode", nullable = false)
    private Career careerCode;

    @OneToMany(mappedBy = "student")
    private Set<Loan> loans = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<Reservation> reservations = new LinkedHashSet<>();

}