package com.ayd.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "book")
@Getter  @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Book {
    @Id
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 255)
    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publicationDate")
    private LocalDate publicationDate;

    @Size(max = 255)
    @Column(name = "publisher")
    private String publisher;

    @Column(name = "status")
    private Boolean status;

    @NotNull
    @Column(name = "availableCopies", nullable = false)
    private Integer availableCopies;

    @OneToMany(mappedBy = "bookCode")
    private Set<Loan> loans = new LinkedHashSet<>();

    @OneToMany(mappedBy = "bookCode")
    private Set<Reservation> reservations = new LinkedHashSet<>();

}