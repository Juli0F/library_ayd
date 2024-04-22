package com.ayd.library.model;


import com.ayd.library.enums.Rol;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Rol role;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Column(name = "username", nullable = false, length = 45, unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "status")
    private Short status;

}
