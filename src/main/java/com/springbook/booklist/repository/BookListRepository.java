package com.springbook.booklist.repository;

import com.springbook.booklist.BookList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookListRepository extends JpaRepository<BookList, Long> {

    List<BookList> findByUserId(Long userId);

    Optional<BookList> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndName(Long userId, String name);
}
