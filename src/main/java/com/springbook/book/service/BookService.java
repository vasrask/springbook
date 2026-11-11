
package com.springbook.book.service;

import com.springbook.book.Book;
import com.springbook.book.BookRepository;
import com.springbook.book.dto.BookRequest;
import com.springbook.book.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {

        Book book = new Book();

        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setDescription(request.description());
        book.setCoverUrl(request.coverUrl());
        book.setGenre(request.genre());
        book.setPublicationYear(request.publicationYear());

        Book savedBook = bookRepository.save(book);

        return toResponse(savedBook);
    }

    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found: " + id)
                );

        return toResponse(book);
    }

    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookResponse> searchBooksByTitle(String title) {

        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BookResponse toResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getCoverUrl(),
                book.getGenre(),
                book.getPublicationYear()
        );
    }
}
