package com.ayd.library.dto.user;

public record UserResponseDto(
        Long userId,
        String role,
        String name,
        String email,
        String username,
        Boolean status) {
}
