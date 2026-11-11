
package com.springbook.reading.dto;

import com.springbook.reading.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserBookRequest(

        @NotNull
        Long bookId,

        @NotNull
        ReadingStatus status,

        @Min(1)
        @Max(5)
        Integer rating,

        LocalDate startedAt,

        LocalDate finishedAt,

        String personalSummary,

        String personalReview

) { }