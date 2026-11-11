package com.springbook.booklist.dto;

import java.time.Instant;

public record BookListResponse(
        Long id,
        String name,
        String description,
        int bookCount,
        Instant createdAt,
        Instant updatedAt
) {}
