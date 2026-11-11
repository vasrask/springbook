
package com.springbook.reading.controller;

import com.springbook.reading.ReadingStatus;
import com.springbook.reading.dto.UserBookRequest;
import com.springbook.reading.dto.UserBookResponse;
import com.springbook.reading.service.UserBookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/library")
public class UserBookController {

    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserBookResponse addBook(
            @PathVariable Long userId,
            @Valid @RequestBody UserBookRequest request) {
        return userBookService.addBookToLibrary(userId, request);
    }

    @GetMapping
    public List<UserBookResponse> getLibrary(
            @PathVariable Long userId) {
        return userBookService.getUserLibrary(userId);
    }

    @GetMapping("/status/{status}")
    public List<UserBookResponse> getBooksByStatus(
            @PathVariable Long userId,
            @PathVariable ReadingStatus status) {
        return userBookService.getUserBooksByStatus(userId, status);
    }
}
