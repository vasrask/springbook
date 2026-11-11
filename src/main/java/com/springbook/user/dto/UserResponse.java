
package com.springbook.user.dto;

public record UserResponse(
        Long id,
        String email,
        String displayName
) { }