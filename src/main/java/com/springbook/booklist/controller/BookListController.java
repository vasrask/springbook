package com.springbook.booklist.controller;

import com.springbook.booklist.dto.BookListItemRequest;
import com.springbook.booklist.dto.BookListItemResponse;
import com.springbook.booklist.dto.BookListRequest;
import com.springbook.booklist.dto.BookListResponse;
import com.springbook.booklist.service.BookListService;
import com.springbook.security.CurrentUserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class BookListController {

    private final BookListService bookListService;
    private final CurrentUserService currentUserService;

    public BookListController(
            BookListService bookListService,
            CurrentUserService currentUserService) {

        this.bookListService = bookListService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookListResponse createBookList(
            Authentication authentication,
            @Valid @RequestBody BookListRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.createBookList(userId, request);
    }

    @GetMapping
    public List<BookListResponse> getAllBookLists(
            Authentication authentication) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.getAllBookLists(userId);
    }

    @GetMapping("/{id}")
    public BookListResponse getBookListById(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.getBookListById(userId, id);
    }

    @PutMapping("/{id}")
    public BookListResponse updateBookList(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody BookListRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.updateBookList(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookList(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        bookListService.deleteBookList(userId, id);
    }

    @PostMapping("/{id}/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookListItemResponse addBookToList(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody BookListItemRequest request) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.addBookToList(
                userId, id, request.userBookId());
    }

    @GetMapping("/{id}/books")
    public List<BookListItemResponse> getBooksInList(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        return bookListService.getBooksInList(userId, id);
    }

    @DeleteMapping("/{id}/books/{userBookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookFromList(
            Authentication authentication,
            @PathVariable Long id,
            @PathVariable Long userBookId) {

        Long userId = currentUserService.getCurrentUserId(authentication);

        bookListService.removeBookFromList(
                userId, id, userBookId);
    }
}
