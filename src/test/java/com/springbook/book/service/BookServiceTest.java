package com.springbook.book.service;

import com.springbook.book.Book;
import com.springbook.book.BookRepository;
import com.springbook.book.dto.BookRequest;
import com.springbook.book.dto.BookResponse;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        book.setIsbn("9780140430646");
        book.setTitle("A Connecticut Yankee in King Arthur's Court");
        book.setAuthor("Mark Twain");
        book.setDescription("A mechanic from Connecticut time-travels to medieval King Arthur's court.");
        book.setGenre("Science Fiction, Fantasy");
        book.setPublicationYear(1889);
    }

    @Test
    void getBookById_shouldReturnBook_whenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("A Connecticut Yankee in King Arthur's Court", response.title());
        assertEquals("Mark Twain", response.author());

        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(99L));

        verify(bookRepository).findById(99L);
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponse> responses = bookService.getAllBooks();

        assertEquals(1, responses.size());
        assertEquals("A Connecticut Yankee in King Arthur's Court", responses.get(0).title());

        verify(bookRepository).findAll();
    }

    @Test
    void searchBooksByTitle_shouldReturnMatchingBooks() {
        when(bookRepository.findByTitleContainingIgnoreCase("yankee")).thenReturn(List.of(book));

        List<BookResponse> responses = bookService.searchBooksByTitle("yankee");

        assertEquals(1, responses.size());
        assertEquals("A Connecticut Yankee in King Arthur's Court", responses.get(0).title());

        verify(bookRepository).findByTitleContainingIgnoreCase("yankee");
    }

    @Test
    void createBook_shouldSaveAndReturnBook() {
        BookRequest request = new BookRequest(
                "9780140430646",
                "A Connecticut Yankee in King Arthur's Court",
                "Mark Twain",
                "A mechanic from Connecticut time-travels to medieval King Arthur's court.",
                null,
                "Science Fiction, Fantasy",
                1889
        );

        when(bookRepository.findByIsbn(request.isbn())).thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.createBook(request);

        assertNotNull(response);
        assertEquals("A Connecticut Yankee in King Arthur's Court", response.title());
        assertEquals("Mark Twain", response.author());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_shouldThrowException_whenIsbnAlreadyExists() {
        BookRequest request = new BookRequest(
                "9780140430646",
                "A Connecticut Yankee in King Arthur's Court",
                "Mark Twain",
                null,
                null,
                "Science Fiction, Fantasy",
                1889
        );

        when(bookRepository.findByIsbn(request.isbn())).thenReturn(Optional.of(book));

        assertThrows(DuplicateResourceException.class, () -> bookService.createBook(request));

        verify(bookRepository, never()).save(any(Book.class));
    }
}