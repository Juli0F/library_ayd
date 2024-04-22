package com.ayd.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Table(name = "career")
@Getter  @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Career {
    @Id
    @Size(max = 10)
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status")
    private Boolean status;
}