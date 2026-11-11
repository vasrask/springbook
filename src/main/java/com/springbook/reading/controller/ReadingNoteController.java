package com.springbook.reading.controller;

import com.springbook.reading.dto.ReadingNoteRequest;
import com.springbook.reading.dto.ReadingNoteResponse;
import com.springbook.reading.service.ReadingNoteService;
import com.springbook.security.CurrentUserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class ReadingNoteController {

    private final ReadingNoteService readingNoteService;
    private final CurrentUserService currentUserService;

    public ReadingNoteController(
            ReadingNoteService readingNoteService,
            CurrentUserService currentUserService) {
        this.readingNoteService = readingNoteService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadingNoteResponse createNote(
            Authentication authentication,
            @Valid @RequestBody ReadingNoteRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return readingNoteService.createNote(userId, request);
    }

    @GetMapping("/book/{userBookId}")
    public List<ReadingNoteResponse> getNotesForBook(
            Authentication authentication,
            @PathVariable Long userBookId) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return readingNoteService.getNotesForBook(userId, userBookId);
    }

    @GetMapping("/{id}")
    public ReadingNoteResponse getNoteById(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return readingNoteService.getNoteById(userId, id);
    }

    @PutMapping("/{id}")
    public ReadingNoteResponse updateNote(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ReadingNoteRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return readingNoteService.updateNote(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        readingNoteService.deleteNote(userId, id);
    }
}
