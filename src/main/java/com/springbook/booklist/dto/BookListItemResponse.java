package com.springbook.booklist.dto;

import java.time.Instant;

public record BookListItemResponse(
        Long id,
        Long userBookId,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String coverUrl,
        Instant addedAt
) {}
