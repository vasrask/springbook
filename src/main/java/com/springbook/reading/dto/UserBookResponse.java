
package com.springbook.reading.dto;

import com.springbook.reading.ReadingStatus;

import java.time.LocalDate;

public record UserBookResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        ReadingStatus status,
        Integer rating,
        LocalDate startedAt,
        LocalDate finishedAt,
        String personalSummary,
        String personalReview
) { }