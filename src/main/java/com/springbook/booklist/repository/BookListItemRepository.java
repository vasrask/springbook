package com.springbook.booklist.repository;

import com.springbook.booklist.BookListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookListItemRepository extends JpaRepository<BookListItem, Long> {

    List<BookListItem> findByBookListId(Long bookListId);

    Optional<BookListItem> findByBookListIdAndUserBookId(
            Long bookListId,
            Long userBookId
    );

    boolean existsByBookListIdAndUserBookId(
            Long bookListId,
            Long userBookId
    );
}