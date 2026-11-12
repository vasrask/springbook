package com.springbook.booklist.service;

import com.springbook.book.Book;
import com.springbook.booklist.BookList;
import com.springbook.booklist.BookListItem;
import com.springbook.booklist.dto.BookListItemResponse;
import com.springbook.booklist.dto.BookListRequest;
import com.springbook.booklist.dto.BookListResponse;
import com.springbook.booklist.repository.BookListItemRepository;
import com.springbook.booklist.repository.BookListRepository;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.exception.ResourceNotFoundException;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.user.User;
import com.springbook.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookListServiceTest {

        @Mock
        private BookListRepository bookListRepository;

        @Mock
        private BookListItemRepository bookListItemRepository;

        @Mock
        private UserBookRepository userBookRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private BookListService bookListService;

        private User user;
        private User anotherUser;
        private Book book;
        private UserBook userBook;
        private UserBook anotherUserBook;
        private BookList bookList;
        private BookListItem bookListItem;

        private final Instant testTime = Instant.parse("2026-01-01T10:00:00Z");

        @BeforeEach
        void setUp() {
                user = new User();
                ReflectionTestUtils.setField(user, "id", 1L);
                user.setEmail("test@example.com");
                user.setDisplayName("Test User");

                anotherUser = new User();
                ReflectionTestUtils.setField(anotherUser, "id", 2L);
                anotherUser.setEmail("another@example.com");
                anotherUser.setDisplayName("Another User");

                book = new Book();
                ReflectionTestUtils.setField(book, "id", 10L);
                book.setTitle("A Connecticut Yankee in King Arthur's Court");
                book.setAuthor("Mark Twain");
                book.setCoverUrl("https://example.com/cover.jpg");

                userBook = new UserBook();
                ReflectionTestUtils.setField(userBook, "id", 100L);
                userBook.setUser(user);
                userBook.setBook(book);

                anotherUserBook = new UserBook();
                ReflectionTestUtils.setField(anotherUserBook, "id", 101L);
                anotherUserBook.setUser(anotherUser);
                anotherUserBook.setBook(book);

                bookList = createBookList(200L, user, "Favorites");

                bookListItem = createBookListItem(300L, userBook);
        }

        private BookList createBookList(Long id, User owner, String name) {
                BookList list = new BookList(owner, name, "My personal collection");

                ReflectionTestUtils.setField(list, "id", id);
                ReflectionTestUtils.setField(list, "createdAt", testTime);
                ReflectionTestUtils.setField(list, "updatedAt", testTime);

                return list;
        }

        private BookListItem createBookListItem(
                Long id,
                UserBook libraryEntry) {

                BookListItem item = new BookListItem(libraryEntry);

                ReflectionTestUtils.setField(item, "id", id);
                ReflectionTestUtils.setField(item, "addedAt", testTime);

                return item;
        }

        @Test
        void createBookList_shouldCreateAndReturnCollection() {
                BookListRequest request = new BookListRequest("Favorites", "My personal collection");

                when(bookListRepository.existsByUserIdAndName(1L, "Favorites")).thenReturn(false);
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(bookListRepository.save(any(BookList.class))).thenReturn(bookList);

                BookListResponse response = bookListService.createBookList(1L, request);

                assertNotNull(response);
                assertEquals(200L, response.id());
                assertEquals("Favorites", response.name());
                assertEquals("My personal collection", response.description());
                assertEquals(0, response.bookCount());

                verify(bookListRepository).save(any(BookList.class));
        }

        @Test
        void createBookList_shouldThrowException_whenNameAlreadyExists() {
                BookListRequest request = new BookListRequest("Favorites", "Another description");

                when(bookListRepository.existsByUserIdAndName(1L, "Favorites")).thenReturn(true);

                assertThrows(DuplicateResourceException.class, () -> bookListService.createBookList(1L, request));

                verify(userRepository, never()).findById(anyLong());
                verify(bookListRepository, never()).save(any(BookList.class));
        }

        @Test
        void createBookList_shouldThrowException_whenUserDoesNotExist() {
                BookListRequest request = new BookListRequest("Favorites", "Description");

                when(bookListRepository.existsByUserIdAndName(99L, "Favorites")).thenReturn(false);
                when(userRepository.findById(99L)).thenReturn(Optional.empty());
                assertThrows(ResourceNotFoundException.class, () -> bookListService.createBookList(99L, request));
                verify(bookListRepository, never()).save(any(BookList.class));
        }

        @Test
        void getAllBookLists_shouldReturnUserCollections() {
                when(bookListRepository.findByUserId(1L)).thenReturn(List.of(bookList));

                List<BookListResponse> responses = bookListService.getAllBookLists(1L);

                assertEquals(1, responses.size());
                assertEquals(200L, responses.get(0).id());
                assertEquals("Favorites", responses.get(0).name());
        }

        @Test
        void getBookListById_shouldReturnCollection_whenOwnedByUser() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));
                BookListResponse response = bookListService.getBookListById(1L, 200L);

                assertNotNull(response);
                assertEquals(200L, response.id());
                assertEquals("Favorites", response.name());
        }

        @Test
        void getBookListById_shouldThrowException_whenNotOwnedByUser() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.empty());
                assertThrows(ResourceNotFoundException.class, () -> bookListService.getBookListById(1L, 200L));
        }

        @Test
        void updateBookList_shouldUpdateCollection() {
                BookListRequest request = new BookListRequest("Science Fiction", "My science fiction books");

                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));
                when(bookListRepository.existsByUserIdAndName(1L, "Science Fiction")).thenReturn(false);

                BookListResponse response = bookListService.updateBookList(1L, 200L, request);

                assertEquals("Science Fiction", response.name());
                assertEquals("My science fiction books", response.description());
                assertEquals("Science Fiction", bookList.getName());
        }

        @Test
        void updateBookList_shouldThrowException_whenNewNameAlreadyExists() {
                BookListRequest request = new BookListRequest("Science Fiction", "Description");

                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));
                when(bookListRepository.existsByUserIdAndName(1L, "Science Fiction")).thenReturn(true);

                assertThrows(
                        DuplicateResourceException.class, () -> bookListService.updateBookList(1L, 200L, request)
                );

                assertEquals("Favorites", bookList.getName());
        }

        @Test
        void deleteBookList_shouldDeleteCollection() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.of(bookList));

                assertDoesNotThrow(() -> bookListService.deleteBookList(1L, 200L));

                verify(bookListRepository).delete(bookList);
        }

        @Test
        void deleteBookList_shouldThrowException_whenNotOwnedByUser() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.empty());
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.deleteBookList(1L, 200L)
                );

                verify(bookListRepository, never()).delete(any(BookList.class));
        }

        @Test
        void addBookToList_shouldAddAndReturnBook() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.of(bookList));

                when(userBookRepository.findById(100L))
                        .thenReturn(Optional.of(userBook));

                when(bookListItemRepository.existsByBookListIdAndUserBookId(200L, 100L))
                        .thenReturn(false);

                when(bookListItemRepository.save(any(BookListItem.class)))
                        .thenAnswer(invocation -> {
                                BookListItem saved = invocation.getArgument(0);
                                ReflectionTestUtils.setField(saved, "id", 300L);
                                ReflectionTestUtils.setField(saved, "addedAt", testTime);
                                return saved;
                        });

                BookListItemResponse response = bookListService.addBookToList(1L, 200L, 100L);

                assertNotNull(response);
                assertEquals(300L, response.id());
                assertEquals(100L, response.userBookId());
                assertEquals(10L, response.bookId());
                assertEquals("A Connecticut Yankee in King Arthur's Court", response.bookTitle());
                assertEquals("Mark Twain", response.bookAuthor());
                assertEquals("https://example.com/cover.jpg", response.coverUrl());

                verify(bookListItemRepository).save(any(BookListItem.class));
        }

        @Test
        void addBookToList_shouldThrowException_whenBookBelongsToAnotherUser() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));
                when(userBookRepository.findById(101L)).thenReturn(Optional.of(anotherUserBook));

                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.addBookToList(1L, 200L, 101L)
                );

                verify(bookListItemRepository, never()).save(any(BookListItem.class));
        }

        @Test
        void addBookToList_shouldThrowException_whenLibraryEntryDoesNotExist() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));

                when(userBookRepository.findById(999L)).thenReturn(Optional.empty());

                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.addBookToList(1L, 200L, 999L)
                );

                verify(bookListItemRepository, never()).save(any(BookListItem.class));
        }

        @Test
        void addBookToList_shouldThrowException_whenBookAlreadyExistsInList() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.of(bookList));

                when(userBookRepository.findById(100L))
                        .thenReturn(Optional.of(userBook));

                when(bookListItemRepository.existsByBookListIdAndUserBookId(200L, 100L))
                        .thenReturn(true);

                assertThrows(
                        DuplicateResourceException.class,
                        () -> bookListService.addBookToList(1L, 200L, 100L)
                );

                verify(bookListItemRepository, never()).save(any(BookListItem.class));
        }

        @Test
        void getBooksInList_shouldReturnCollectionBooks() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.of(bookList));

                when(bookListItemRepository.findByBookListId(200L)).thenReturn(List.of(bookListItem));

                List<BookListItemResponse> responses = bookListService.getBooksInList(1L, 200L);

                assertEquals(1, responses.size());
                assertEquals(300L, responses.get(0).id());
                assertEquals(100L, responses.get(0).userBookId());
                assertEquals(10L, responses.get(0).bookId());
                assertEquals("Mark Twain", responses.get(0).bookAuthor());
        }

        @Test
        void getBooksInList_shouldThrowException_whenCollectionNotOwnedByUser() {

                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.empty());

                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.getBooksInList(1L, 200L)
                );

                verify(bookListItemRepository, never()).findByBookListId(anyLong());
        }

        @Test
        void removeBookFromList_shouldDeleteCollectionItem() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.of(bookList));
                when(bookListItemRepository.findByBookListIdAndUserBookId(200L, 100L))
                        .thenReturn(Optional.of(bookListItem));

                assertDoesNotThrow(() -> bookListService.removeBookFromList(1L, 200L, 100L)
                );

                verify(bookListItemRepository).delete(bookListItem);
        }


        @Test
        void removeBookFromList_shouldThrowException_whenItemDoesNotExist() {
                when(bookListRepository.findByIdAndUserId(200L, 1L))
                        .thenReturn(Optional.of(bookList));
                when(bookListItemRepository.findByBookListIdAndUserBookId(200L, 999L))
                        .thenReturn(Optional.empty());

                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.removeBookFromList(1L, 200L, 999L)
                );

                verify(bookListItemRepository, never()).delete(any(BookListItem.class));
        }


        @Test
        void removeBookFromList_shouldThrowException_whenCollectionNotOwnedByUser() {
                when(bookListRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.empty());

                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookListService.removeBookFromList(1L, 200L, 100L)
                );

                verify(bookListItemRepository, never()).findByBookListIdAndUserBookId(anyLong(), anyLong());
                verify(bookListItemRepository, never()).delete(any(BookListItem.class));
        }
}