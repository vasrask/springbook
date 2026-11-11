
package com.springbook.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @Size(max = 20)
        String isbn,

        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        @Size(max = 255)
        String author,

        String description,

        String coverUrl,

        @Size(max = 100)
        String genre,

        @Min(1)
        @Max(2100)
        Integer publicationYear

) { }