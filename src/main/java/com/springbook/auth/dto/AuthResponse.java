
package com.springbook.auth.dto;

public record AuthResponse(
        Long id,
        String email,
        String displayName,
        String token
) {}
