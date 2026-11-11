
package com.springbook.reading.service;

import com.springbook.book.Book;
import com.springbook.book.BookRepository;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.reading.ReadingStatus;
import com.springbook.reading.dto.UserBookRequest;
import com.springbook.reading.dto.UserBookResponse;
import com.springbook.user.User;
import com.springbook.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserBookService {

    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserBookService(
            UserBookRepository userBookRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.userBookRepository = userBookRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public UserBookResponse addBookToLibrary(
            Long userId,
            UserBookRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found")
                );

        if (userBookRepository.existsByUserIdAndBookId(
                userId, request.bookId())) {

            throw new IllegalStateException(
                    "This book is already in the user's library"
            );
        }

        UserBook userBook = new UserBook();

        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setStatus(request.status());
        userBook.setRating(request.rating());
        userBook.setStartedAt(request.startedAt());
        userBook.setFinishedAt(request.finishedAt());
        userBook.setPersonalSummary(request.personalSummary());
        userBook.setPersonalReview(request.personalReview());

        UserBook saved = userBookRepository.save(userBook);

        return toResponse(saved);
    }

    public List<UserBookResponse> getUserLibrary(Long userId) {

        return userBookRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UserBookResponse> getUserBooksByStatus(
            Long userId,
            ReadingStatus status
    ) {

        return userBookRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserBookResponse toResponse(UserBook userBook) {

        Book book = userBook.getBook();

        return new UserBookResponse(
                userBook.getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                userBook.getStatus(),
                userBook.getRating(),
                userBook.getStartedAt(),
                userBook.getFinishedAt(),
                userBook.getPersonalSummary(),
                userBook.getPersonalReview()
        );
    }
}
