package com.springbook.book;

import com.springbook.testutil.RepositoryTestData;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByIsbn_shouldReturnBook_whenIsbnExists() {
        RepositoryTestData.createBook(
                entityManager,
                "9780000000001",
                "The Hobbit",
                "J.R.R. Tolkien"
        );

        Optional<Book> result = bookRepository.findByIsbn("9780000000001");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("The Hobbit");
    }

    @Test
    void findByIsbn_shouldReturnEmpty_whenIsbnDoesNotExist() {
        Optional<Book> result = bookRepository.findByIsbn("9780000000099");

        assertThat(result).isEmpty();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldFindMatchingBooks() {
        RepositoryTestData.createBook(
                entityManager,
                "9780000000001",
                "The Hobbit",
                "J.R.R. Tolkien"
        );

        RepositoryTestData.createBook(
                entityManager,
                "9780000000002",
                "The Fellowship of the Ring",
                "J.R.R. Tolkien"
        );

        RepositoryTestData.createBook(
                entityManager,
                "9780000000003",
                "Dune",
                "Frank Herbert"
        );

        List<Book> result = bookRepository.findByTitleContainingIgnoreCase("hobbit");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("The Hobbit");
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnEmpty_whenNoMatchExists() {
        List<Book> result = bookRepository.findByTitleContainingIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByAuthorContainingIgnoreCase_shouldFindMatchingBooks() {
        RepositoryTestData.createBook(
                entityManager,
                "9780000000001",
                "The Hobbit",
                "J.R.R. Tolkien"
        );

        RepositoryTestData.createBook(
                entityManager,
                "9780000000002",
                "The Fellowship of the Ring",
                "J.R.R. Tolkien"
        );

        RepositoryTestData.createBook(
                entityManager,
                "9780000000003",
                "Dune",
                "Frank Herbert"
        );

        List<Book> result = bookRepository.findByAuthorContainingIgnoreCase("tolkien");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getAuthor).containsOnly("J.R.R. Tolkien");
    }

    @Test
    void findByAuthorContainingIgnoreCase_shouldReturnEmpty_whenNoMatchExists() {
        List<Book> result = bookRepository.findByAuthorContainingIgnoreCase("unknown author");

        assertThat(result).isEmpty();
    }
}