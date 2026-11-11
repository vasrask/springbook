
package com.springbook.reading.service;

import com.springbook.exception.ResourceNotFoundException;
import com.springbook.reading.ReadingNote;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.reading.dto.ReadingNoteRequest;
import com.springbook.reading.dto.ReadingNoteResponse;
import com.springbook.reading.repository.ReadingNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReadingNoteService {

    private final ReadingNoteRepository readingNoteRepository;
    private final UserBookRepository userBookRepository;

    public ReadingNoteService(
            ReadingNoteRepository readingNoteRepository,
            UserBookRepository userBookRepository) {
        this.readingNoteRepository = readingNoteRepository;
        this.userBookRepository = userBookRepository;
    }

    @Transactional
    public ReadingNoteResponse createNote(
            Long userId,
            ReadingNoteRequest request) {

        UserBook userBook = getUserBook(
                userId,
                request.userBookId()
        );

        ReadingNote note = new ReadingNote();
        note.setUserBook(userBook);
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setNoteType(request.noteType());

        ReadingNote savedNote = readingNoteRepository.save(note);

        return toResponse(savedNote);
    }

    public List<ReadingNoteResponse> getNotesForBook(
            Long userId,
            Long userBookId) {

        getUserBook(userId, userBookId);

        return readingNoteRepository
                .findByUserBookId(userBookId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReadingNoteResponse getNoteById(
            Long userId,
            Long noteId) {

        ReadingNote note = getNote(noteId);
        verifyOwnership(note, userId);

        return toResponse(note);
    }

    @Transactional
    public ReadingNoteResponse updateNote(
            Long userId,
            Long noteId,
            ReadingNoteRequest request) {

        ReadingNote note = getNote(noteId);
        verifyOwnership(note, userId);

        UserBook requestedUserBook = getUserBook(
                userId,
                request.userBookId()
        );

        note.setUserBook(requestedUserBook);
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setNoteType(request.noteType());

        return toResponse(note);
    }

    @Transactional
    public void deleteNote(Long userId, Long noteId) {

        ReadingNote note = getNote(noteId);
        verifyOwnership(note, userId);

        readingNoteRepository.delete(note);
    }

    private UserBook getUserBook(Long userId, Long userBookId) {

        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Library entry not found"
                ));

        if (!userBook.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Library entry not found"
            );
        }

        return userBook;
    }

    private ReadingNote getNote(Long noteId) {

        return readingNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reading note not found"
                ));
    }

    private void verifyOwnership(ReadingNote note, Long userId) {

        if (!note.getUserBook().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Reading note not found"
            );
        }
    }

    private ReadingNoteResponse toResponse(ReadingNote note) {

        UserBook userBook = note.getUserBook();

        return new ReadingNoteResponse(
                note.getId(),
                userBook.getId(),
                userBook.getBook().getTitle(),
                note.getTitle(),
                note.getContent(),
                note.getNoteType(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}