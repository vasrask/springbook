package com.springbook.booklist;

import com.springbook.reading.UserBook;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "book_list_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_book_list_items_list_book",
                        columnNames = {"book_list_id", "user_book_id"}
                )
        }
)
public class BookListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_list_id", nullable = false)
    private BookList bookList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_book_id", nullable = false)
    private UserBook userBook;

    @Column(name = "added_at", nullable = false, updatable = false)
    private Instant addedAt;

    protected BookListItem() {
    }

    public BookListItem(UserBook userBook) {
        this.userBook = userBook;
    }

    @PrePersist
    protected void onCreate() {
        addedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public BookList getBookList() {
        return bookList;
    }

    public UserBook getUserBook() {
        return userBook;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setBookList(BookList bookList) {
        this.bookList = bookList;
    }
}
