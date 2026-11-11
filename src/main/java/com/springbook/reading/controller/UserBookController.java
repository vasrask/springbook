package com.springbook.reading.controller;

import com.springbook.reading.ReadingStatus;
import com.springbook.reading.dto.UserBookRequest;
import com.springbook.reading.dto.UserBookResponse;
import com.springbook.reading.service.UserBookService;
import com.springbook.security.CurrentUserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class UserBookController {

    private final UserBookService userBookService;
    private final CurrentUserService currentUserService;

    public UserBookController(
            UserBookService userBookService,
            CurrentUserService currentUserService) {
        this.userBookService = userBookService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserBookResponse addBook(
            Authentication authentication,
            @Valid @RequestBody UserBookRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return userBookService.addBookToLibrary(userId, request);
    }

    @GetMapping
    public List<UserBookResponse> getLibrary(
            Authentication authentication) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return userBookService.getUserLibrary(userId);
    }

    @GetMapping("/status/{status}")
    public List<UserBookResponse> getBooksByStatus(
            Authentication authentication,
            @PathVariable ReadingStatus status) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return userBookService.getUserBooksByStatus(userId, status);
    }
}