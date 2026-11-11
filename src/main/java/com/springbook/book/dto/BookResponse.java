
package com.springbook.book.dto;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String description,
        String coverUrl,
        String genre,
        Integer publicationYear
) { }