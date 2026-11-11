package com.springbook.reading.dto;

import com.springbook.reading.NoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReadingNoteRequest(

        @NotNull
        Long userBookId,

        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        String content,

        @NotNull
        NoteType noteType

) {}
