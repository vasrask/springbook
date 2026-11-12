
package com.springbook.reading.service;

import com.springbook.book.Book;
import com.springbook.book.BookRepository;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.exception.ResourceNotFoundException;
import com.springbook.reading.ReadingStatus;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.reading.dto.UserBookRequest;
import com.springbook.reading.dto.UserBookResponse;
import com.springbook.user.User;
import com.springbook.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBookServiceTest {

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserBookService userBookService;

    private User user;
    private Book book;
    private UserBook userBook;

    @BeforeEach
    void setUp() {

        user = new User();

        ReflectionTestUtils.setField(user, "id", 1L);

        user.setEmail("test@example.com");
        user.setDisplayName("Test User");

        book = new Book();

        ReflectionTestUtils.setField(book, "id", 10L);

        book.setTitle("A Connecticut Yankee in King Arthur's Court");
        book.setAuthor("Mark Twain");
        book.setIsbn("9780134685991");

        userBook = new UserBook();

        ReflectionTestUtils.setField(userBook, "id", 100L);

        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setStatus(ReadingStatus.CURRENTLY_READING);
        userBook.setRating(4);
    }

    @Test
    void addBookToLibrary_shouldSaveAndReturnBook() {

        UserBookRequest request = new UserBookRequest(
                10L,
                ReadingStatus.CURRENTLY_READING,
                4,
                null,
                null,
                "Interesting book",
                "Enjoying it so far"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        when(userBookRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(false);

        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);

        UserBookResponse response = userBookService.addBookToLibrary(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(10L, response.bookId());
        assertEquals("A Connecticut Yankee in King Arthur's Court", response.bookTitle());
        assertEquals("Mark Twain", response.bookAuthor());
        assertEquals(ReadingStatus.CURRENTLY_READING, response.status());
        assertEquals(4, response.rating());

        verify(userBookRepository).save(any(UserBook.class));
    }

    @Test
    void addBookToLibrary_shouldThrowException_whenUserDoesNotExist() {

        UserBookRequest request = new UserBookRequest(
                10L,
                ReadingStatus.CURRENTLY_READING,
                4,
                null,
                null,
                null,
                null
        );

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userBookService.addBookToLibrary(99L, request)
        );

        verify(bookRepository, never()).findById(anyLong());
        verify(userBookRepository, never()).save(any(UserBook.class));
    }

    @Test
    void addBookToLibrary_shouldThrowException_whenBookDoesNotExist() {

        UserBookRequest request = new UserBookRequest(
                99L,
                ReadingStatus.CURRENTLY_READING,
                4,
                null,
                null,
                null,
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userBookService.addBookToLibrary(1L, request));

        verify(userBookRepository, never()).save(any(UserBook.class));
    }

    @Test
    void addBookToLibrary_shouldThrowException_whenBookAlreadyExists() {

        UserBookRequest request = new UserBookRequest(
                10L,
                ReadingStatus.CURRENTLY_READING,
                4,
                null,
                null,
                null,
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        when(userBookRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userBookService.addBookToLibrary(1L, request));

        verify(userBookRepository, never()).save(any(UserBook.class));
    }

    @Test
    void getUserLibrary_shouldReturnAllUserBooks() {

        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook));

        List<UserBookResponse> responses = userBookService.getUserLibrary(1L);

        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).id());
        assertEquals("A Connecticut Yankee in King Arthur's Court", responses.get(0).bookTitle());

        verify(userBookRepository).findByUserId(1L);
    }

    @Test
    void getUserLibrary_shouldReturnEmptyList_whenLibraryIsEmpty() {

        when(userBookRepository.findByUserId(1L)).thenReturn(List.of());

        List<UserBookResponse> responses = userBookService.getUserLibrary(1L);

        assertTrue(responses.isEmpty());

        verify(userBookRepository).findByUserId(1L);
    }

    @Test
    void getUserBooksByStatus_shouldReturnBooksWithMatchingStatus() {

        when(userBookRepository.findByUserIdAndStatus(1L, ReadingStatus.CURRENTLY_READING))
                .thenReturn(List.of(userBook)
                );

        List<UserBookResponse> responses =
                userBookService.getUserBooksByStatus(1L, ReadingStatus.CURRENTLY_READING);

        assertEquals(1, responses.size());
        assertEquals(ReadingStatus.CURRENTLY_READING, responses.get(0).status());

        verify(userBookRepository).findByUserIdAndStatus(1L, ReadingStatus.CURRENTLY_READING);
    }

    @Test
    void getUserBooksByStatus_shouldReturnEmptyList_whenNoBooksMatch() {

        when(userBookRepository.findByUserIdAndStatus(1L, ReadingStatus.READ)).thenReturn(List.of());

        List<UserBookResponse> responses =
                userBookService.getUserBooksByStatus(1L, ReadingStatus.READ);

        assertTrue(responses.isEmpty());

        verify(userBookRepository).findByUserIdAndStatus(1L, ReadingStatus.READ);
    }
}