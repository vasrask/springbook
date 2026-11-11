package com.springbook.booklist;

import com.springbook.reading.UserBook;
import com.springbook.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "book_lists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_book_lists_user_name",
                        columnNames = {"user_id", "name"}
                )
        }
)
public class BookList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "bookList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BookListItem> items = new ArrayList<>();

    protected BookList() {
    }

    public BookList(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<BookListItem> getItems() {
        return items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addItem(BookListItem item) {
        items.add(item);
        item.setBookList(this);
    }

    public void removeItem(BookListItem item) {
        items.remove(item);
        item.setBookList(null);
    }
}
