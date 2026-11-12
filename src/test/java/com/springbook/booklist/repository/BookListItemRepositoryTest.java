package com.springbook.booklist.repository;

import com.springbook.book.Book;
import com.springbook.booklist.BookList;
import com.springbook.booklist.BookListItem;
import com.springbook.reading.ReadingStatus;
import com.springbook.reading.UserBook;
import com.springbook.testutil.RepositoryTestData;
import com.springbook.user.User;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookListItemRepositoryTest {

    @Autowired
    private BookListItemRepository bookListItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByBookListId_shouldReturnItemsInList() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        BookList bookList = RepositoryTestData.createBookList(entityManager, user, "Favorites");

        Book book1 = RepositoryTestData.createBook(entityManager, "9780000000001", "Book One", "Author One");
        Book book2 = RepositoryTestData.createBook(entityManager, "9780000000002", "Book Two", "Author Two");

        UserBook userBook1 = RepositoryTestData.createUserBook(entityManager, user, book1, ReadingStatus.READ);
        UserBook userBook2 = RepositoryTestData.createUserBook(entityManager, user, book2, ReadingStatus.READ);
        RepositoryTestData.createBookListItem(entityManager, bookList, userBook1);
        RepositoryTestData.createBookListItem(entityManager, bookList, userBook2);

        List<BookListItem> result = bookListItemRepository.findByBookListId(bookList.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByBookListId_shouldReturnEmpty_whenListHasNoItems() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");
        BookList bookList = RepositoryTestData.createBookList(entityManager, user, "Empty List");

        List<BookListItem> result = bookListItemRepository.findByBookListId(bookList.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByBookListIdAndUserBookId_shouldReturnMatchingItem() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        BookList bookList = RepositoryTestData.createBookList(entityManager, user, "Favorites");
        Book book = RepositoryTestData.createBook(entityManager, "9780000000001", "The Hobbit", "Tolkien");
        UserBook userBook = RepositoryTestData.createUserBook(entityManager, user, book, ReadingStatus.READ);
        BookListItem item = RepositoryTestData.createBookListItem(entityManager, bookList, userBook);

        Optional<BookListItem> result = bookListItemRepository.findByBookListIdAndUserBookId(bookList.getId(), userBook.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(item.getId());
    }

    @Test
    void findByBookListIdAndUserBookId_shouldReturnEmpty_whenCombinationDoesNotExist() {
        Optional<BookListItem> result = bookListItemRepository.findByBookListIdAndUserBookId(99999L, 99999L);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByBookListIdAndUserBookId_shouldReturnTrue_whenItemExists() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        BookList bookList = RepositoryTestData.createBookList(entityManager, user, "Favorites");
        Book book = RepositoryTestData.createBook(entityManager, "9780000000001", "The Hobbit", "Tolkien");
        UserBook userBook = RepositoryTestData.createUserBook(entityManager, user, book, ReadingStatus.READ);
        RepositoryTestData.createBookListItem(entityManager, bookList, userBook);

        boolean result = bookListItemRepository.existsByBookListIdAndUserBookId(bookList.getId(), userBook.getId());

        assertThat(result).isTrue();
    }

    @Test
    void existsByBookListIdAndUserBookId_shouldReturnFalse_whenItemDoesNotExist() {
        boolean result = bookListItemRepository.existsByBookListIdAndUserBookId(99999L, 99999L);

        assertThat(result).isFalse();
    }
}