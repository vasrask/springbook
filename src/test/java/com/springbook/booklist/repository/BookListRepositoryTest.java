package com.springbook.booklist.repository;

import com.springbook.booklist.BookList;
import com.springbook.booklist.repository.BookListRepository;
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
class BookListRepositoryTest {

    @Autowired
    private BookListRepository bookListRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByUserId_shouldReturnOnlyListsBelongingToUser() {
        User user1 = RepositoryTestData.createUser(entityManager, "user1@example.com");

        User user2 = RepositoryTestData.createUser(entityManager, "user2@example.com");

        RepositoryTestData.createBookList(entityManager, user1, "Favorites");
        RepositoryTestData.createBookList(entityManager, user1, "Fantasy Books");
        RepositoryTestData.createBookList(entityManager, user2, "Reading Wishlist");

        List<BookList> result = bookListRepository.findByUserId(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookList::getName).containsExactlyInAnyOrder("Favorites", "Fantasy Books");
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoLists() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        List<BookList> result = bookListRepository.findByUserId(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdAndUserId_shouldReturnList_whenBothIdsMatch() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        BookList bookList = RepositoryTestData.createBookList(entityManager, user, "Favorites");

        Optional<BookList> result = bookListRepository.findByIdAndUserId(bookList.getId(), user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Favorites");
    }

    @Test
    void findByIdAndUserId_shouldReturnEmpty_whenUserDoesNotOwnList() {
        User owner = RepositoryTestData.createUser(entityManager, "owner@example.com");

        User anotherUser = RepositoryTestData.createUser(entityManager, "another@example.com");

        BookList bookList = RepositoryTestData.createBookList(entityManager, owner, "Private List");

        Optional<BookList> result = bookListRepository.findByIdAndUserId(bookList.getId(), anotherUser.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUserIdAndName_shouldReturnTrue_whenListExists() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        RepositoryTestData.createBookList(entityManager, user, "Favorites");

        boolean result = bookListRepository.existsByUserIdAndName(user.getId(), "Favorites");

        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndName_shouldReturnFalse_whenListDoesNotExist() {
        User user = RepositoryTestData.createUser(entityManager, "reader@example.com");

        boolean result = bookListRepository.existsByUserIdAndName(user.getId(), "Missing List");

        assertThat(result).isFalse();
    }
}