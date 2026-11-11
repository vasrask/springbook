package com.springbook.booklist.service;

import com.springbook.booklist.BookList;
import com.springbook.booklist.BookListItem;
import com.springbook.booklist.dto.BookListItemResponse;
import com.springbook.booklist.dto.BookListRequest;
import com.springbook.booklist.dto.BookListResponse;
import com.springbook.booklist.repository.BookListItemRepository;
import com.springbook.booklist.repository.BookListRepository;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.exception.ResourceNotFoundException;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.user.User;
import com.springbook.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookListService {

    private final BookListRepository bookListRepository;
    private final BookListItemRepository bookListItemRepository;
    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;

    public BookListService(
            BookListRepository bookListRepository,
            BookListItemRepository bookListItemRepository,
            UserBookRepository userBookRepository,
            UserRepository userRepository) {

        this.bookListRepository = bookListRepository;
        this.bookListItemRepository = bookListItemRepository;
        this.userBookRepository = userBookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookListResponse createBookList(
            Long userId,
            BookListRequest request) {

        if (bookListRepository.existsByUserIdAndName(
                userId, request.name())) {
            throw new DuplicateResourceException(
                    "A collection with this name already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        BookList bookList = new BookList(
                user,
                request.name(),
                request.description()
        );

        BookList savedBookList = bookListRepository.save(bookList);

        return toResponse(savedBookList);
    }

    public List<BookListResponse> getAllBookLists(Long userId) {

        return bookListRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BookListResponse getBookListById(
            Long userId,
            Long bookListId) {

        BookList bookList = getOwnedBookList(userId, bookListId);

        return toResponse(bookList);
    }

    @Transactional
    public BookListResponse updateBookList(
            Long userId,
            Long bookListId,
            BookListRequest request) {

        BookList bookList = getOwnedBookList(userId, bookListId);

        if (!bookList.getName().equals(request.name())
                && bookListRepository.existsByUserIdAndName(
                userId, request.name())) {

            throw new DuplicateResourceException(
                    "A collection with this name already exists");
        }

        bookList.setName(request.name());
        bookList.setDescription(request.description());

        return toResponse(bookList);
    }

    @Transactional
    public void deleteBookList(Long userId, Long bookListId) {

        BookList bookList = getOwnedBookList(userId, bookListId);

        bookListRepository.delete(bookList);
    }

    @Transactional
    public BookListItemResponse addBookToList(
            Long userId,
            Long bookListId,
            Long userBookId) {

        BookList bookList = getOwnedBookList(userId, bookListId);

        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Library entry not found"));

        if (!userBook.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Library entry not found");
        }

        if (bookListItemRepository.existsByBookListIdAndUserBookId(
                bookListId, userBookId)) {

            throw new DuplicateResourceException(
                    "This book is already in the collection");
        }

        BookListItem item = new BookListItem(userBook);
        bookList.addItem(item);

        BookListItem savedItem = bookListItemRepository.save(item);

        return toItemResponse(savedItem);
    }

    public List<BookListItemResponse> getBooksInList(
            Long userId,
            Long bookListId) {

        getOwnedBookList(userId, bookListId);

        return bookListItemRepository.findByBookListId(bookListId)
                .stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Transactional
    public void removeBookFromList(
            Long userId,
            Long bookListId,
            Long userBookId) {

        getOwnedBookList(userId, bookListId);

        BookListItem item = bookListItemRepository
                .findByBookListIdAndUserBookId(
                        bookListId, userBookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found in collection"));

        bookListItemRepository.delete(item);
    }

    private BookList getOwnedBookList(
            Long userId,
            Long bookListId) {

        return bookListRepository
                .findByIdAndUserId(bookListId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Collection not found"));
    }

    private BookListResponse toResponse(BookList bookList) {

        return new BookListResponse(
                bookList.getId(),
                bookList.getName(),
                bookList.getDescription(),
                bookList.getItems().size(),
                bookList.getCreatedAt(),
                bookList.getUpdatedAt()
        );
    }

    private BookListItemResponse toItemResponse(BookListItem item) {

        UserBook userBook = item.getUserBook();

        return new BookListItemResponse(
                item.getId(),
                userBook.getId(),
                userBook.getBook().getId(),
                userBook.getBook().getTitle(),
                userBook.getBook().getAuthor(),
                userBook.getBook().getCoverUrl(),
                item.getAddedAt()
        );
    }
}
