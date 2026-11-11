package com.springbook.reading.dto;

import com.springbook.reading.NoteType;

import java.time.Instant;

public record ReadingNoteResponse(

        Long id,
        Long userBookId,
        String bookTitle,
        String title,
        String content,
        NoteType noteType,
        Instant createdAt,
        Instant updatedAt

) {}
