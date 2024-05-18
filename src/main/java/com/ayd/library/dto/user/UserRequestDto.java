package com.ayd.library.dto.user;


import com.ayd.library.enums.Rol;
import com.ayd.library.model.User;

public record UserRequestDto(String name, String username, String password, String email, String role) {

    public User toUser() {
        return User.builder()
                .name(name)
                .email(email)
                .username(username)
                .password(password)
                .role(Rol.valueOf(role.toUpperCase()))
                .status((short)1)
                .build();
    }
}
