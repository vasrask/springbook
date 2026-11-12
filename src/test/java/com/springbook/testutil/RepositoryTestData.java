package com.springbook.testutil;

import com.springbook.book.Book;
import com.springbook.booklist.BookList;
import com.springbook.booklist.BookListItem;
import com.springbook.reading.NoteType;
import com.springbook.reading.ReadingNote;
import com.springbook.reading.ReadingStatus;
import com.springbook.reading.UserBook;
import com.springbook.user.User;

import jakarta.persistence.EntityManager;

import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;

public final class RepositoryTestData {

    private RepositoryTestData() {}

    private static <T> T createEntity(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();

            constructor.setAccessible(true);

            return constructor.newInstance();

        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create " + type.getSimpleName(), exception);
        }
    }

    private static void set(Object entity, String field, Object value) {
        ReflectionTestUtils.setField(entity, field, value);
    }

    public static User createUser(EntityManager entityManager, String email) {
        User user = createEntity(User.class);

        set(user, "email", email);
        set(user, "passwordHash", "test-password-hash");
        set(user, "displayName", "Test User");

        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    public static Book createBook(EntityManager entityManager,
            String isbn,
            String title,
            String author
    ) {
        Book book = createEntity(Book.class);

        set(book, "isbn", isbn);
        set(book, "title", title);
        set(book, "author", author);
        set(book, "description", "Test description");
        set(book, "genre", "Fantasy");
        set(book, "publicationYear", 2024);

        entityManager.persist(book);
        entityManager.flush();

        return book;
    }

    public static UserBook createUserBook(EntityManager entityManager,
            User user,
            Book book,
            ReadingStatus status
    ) {
        UserBook userBook = createEntity(UserBook.class);

        set(userBook, "user", user);
        set(userBook, "book", book);
        set(userBook, "status", status);

        entityManager.persist(userBook);
        entityManager.flush();

        return userBook;
    }

    public static ReadingNote createReadingNote(EntityManager entityManager,
            UserBook userBook,
            String title,
            NoteType noteType
    ) {
        ReadingNote note = createEntity(ReadingNote.class);

        set(note, "userBook", userBook);
        set(note, "title", title);
        set(note, "content", "Test note content");
        set(note, "noteType", noteType);

        entityManager.persist(note);
        entityManager.flush();

        return note;
    }

    public static BookList createBookList(EntityManager entityManager,
            User user,
            String name
    ) {
        BookList bookList = createEntity(BookList.class);

        set(bookList, "user", user);
        set(bookList, "name", name);
        set(bookList, "description", "Test list description");

        entityManager.persist(bookList);
        entityManager.flush();

        return bookList;
    }

    public static BookListItem createBookListItem(EntityManager entityManager,
            BookList bookList,
            UserBook userBook
    ) {
        BookListItem item = createEntity(BookListItem.class);

        set(item, "bookList", bookList);
        set(item, "userBook", userBook);

        entityManager.persist(item);
        entityManager.flush();

        return item;
    }
}