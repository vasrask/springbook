package com.springbook.reading;

import com.springbook.book.Book;
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
class UserBookRepositoryTest {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByUserId_shouldReturnOnlyBooksBelongingToUser() {
        User user1 = RepositoryTestData.createUser(entityManager, "user1@example.com");

        User user2 = RepositoryTestData.createUser(entityManager, "user2@example.com");

        Book book1 = RepositoryTestData.createBook(entityManager, "9780000000001", "Book One", "Author One");
        Book book2 = RepositoryTestData.createBook(entityManager, "9780000000002", "Book Two", "Author Two");

        RepositoryTestData.createUserBook(entityManager, user1, book1, ReadingStatus.READ);
        RepositoryTestData.createUserBook(entityManager, user2, book2, ReadingStatus.CURRENTLY_READING);

        List<UserBook> result = userBookRepository.findByUserId(user1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBook().getTitle()).isEqualTo("Book One");
    }

    @Test
    void findByUserIdAndBookId_shouldReturnMatchingUserBook() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        Book book = RepositoryTestData.createBook(entityManager, "9780000000001", "The Hobbit", "Tolkien");

        RepositoryTestData.createUserBook(entityManager, user, book, ReadingStatus.READ);

        Optional<UserBook> result = userBookRepository.findByUserIdAndBookId(user.getId(), book.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void findByUserIdAndBookId_shouldReturnEmpty_whenCombinationDoesNotExist() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        Optional<UserBook> result = userBookRepository.findByUserIdAndBookId(user.getId(), 99999L);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUserIdAndBookId_shouldReturnTrue_whenCombinationExists() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");
        Book book = RepositoryTestData.createBook(entityManager, "9780000000001", "The Hobbit", "Tolkien");

        RepositoryTestData.createUserBook(entityManager, user, book, ReadingStatus.READ);

        boolean result = userBookRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndBookId_shouldReturnFalse_whenCombinationDoesNotExist() {
        boolean result = userBookRepository.existsByUserIdAndBookId(99999L, 99999L);

        assertThat(result).isFalse();
    }

    @Test
    void findByUserIdAndStatus_shouldReturnOnlyMatchingStatus() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        Book book1 = RepositoryTestData.createBook(entityManager, "9780000000001", "Book One", "Author One");
        Book book2 = RepositoryTestData.createBook(entityManager, "9780000000002", "Book Two", "Author Two");

        RepositoryTestData.createUserBook(entityManager, user, book1, ReadingStatus.READ);

        RepositoryTestData.createUserBook(entityManager, user, book2, ReadingStatus.WANT_TO_READ);

        List<UserBook> result = userBookRepository.findByUserIdAndStatus(user.getId(), ReadingStatus.READ);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReadingStatus.READ);
    }
}