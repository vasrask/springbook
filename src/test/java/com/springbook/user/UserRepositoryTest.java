
package com.springbook.user;

import com.springbook.testutil.RepositoryTestData;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        RepositoryTestData.createUser(entityManager, "reader@example.com");

        Optional<User> result = userRepository.findByEmail("reader@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("reader@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        RepositoryTestData.createUser(entityManager, "reader@example.com");

        boolean result = userRepository.existsByEmail("reader@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        boolean result = userRepository.existsByEmail("missing@example.com");

        assertThat(result).isFalse();
    }
}