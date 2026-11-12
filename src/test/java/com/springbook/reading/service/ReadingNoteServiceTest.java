package com.springbook.reading.service;

import com.springbook.book.Book;
import com.springbook.exception.ResourceNotFoundException;
import com.springbook.reading.NoteType;
import com.springbook.reading.ReadingNote;
import com.springbook.reading.UserBook;
import com.springbook.reading.UserBookRepository;
import com.springbook.reading.dto.ReadingNoteRequest;
import com.springbook.reading.dto.ReadingNoteResponse;
import com.springbook.reading.repository.ReadingNoteRepository;
import com.springbook.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingNoteServiceTest {

    @Mock
    private ReadingNoteRepository readingNoteRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @InjectMocks
    private ReadingNoteService readingNoteService;

    private User user;
    private User anotherUser;
    private Book book;
    private UserBook userBook;
    private UserBook anotherUserBook;
    private ReadingNote note;

    private final Instant testTime = Instant.parse("2026-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");

        anotherUser = new User();
        ReflectionTestUtils.setField(anotherUser, "id", 2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setDisplayName("Another User");

        book = new Book();
        ReflectionTestUtils.setField(book, "id", 10L);
        book.setTitle("A Connecticut Yankee in King Arthur's Court");
        book.setAuthor("Mark Twain");

        userBook = new UserBook();
        ReflectionTestUtils.setField(userBook, "id", 100L);
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setStatus(com.springbook.reading.ReadingStatus.READ);

        anotherUserBook = new UserBook();
        ReflectionTestUtils.setField(anotherUserBook, "id", 101L);
        anotherUserBook.setUser(anotherUser);
        anotherUserBook.setBook(book);

        note = new ReadingNote();
        ReflectionTestUtils.setField(note, "id", 200L);
        ReflectionTestUtils.setField(note, "createdAt", testTime);
        ReflectionTestUtils.setField(note, "updatedAt", testTime);

        note.setUserBook(userBook);
        note.setTitle("My favorite quote");
        note.setContent("A memorable passage from the book.");
        note.setNoteType(NoteType.QUOTE);
    }

    @Test
    void createNote_shouldSaveAndReturnNote() {
        ReadingNoteRequest request = new ReadingNoteRequest(100L,
                "My favorite quote",
                "A memorable passage from the book.",
                NoteType.QUOTE
        );

        when(userBookRepository.findById(100L)).thenReturn(Optional.of(userBook));

        when(readingNoteRepository.save(any(ReadingNote.class))).thenAnswer(invocation -> {
                    ReadingNote saved = invocation.getArgument(0);

                    ReflectionTestUtils.setField(saved, "id", 200L);
                    ReflectionTestUtils.setField(
                            saved, "createdAt", testTime
                    );
                    ReflectionTestUtils.setField(
                            saved, "updatedAt", testTime
                    );

                    return saved;
                });

        ReadingNoteResponse response = readingNoteService.createNote(1L, request);

        assertNotNull(response);
        assertEquals(200L, response.id());
        assertEquals(100L, response.userBookId());
        assertEquals("My favorite quote", response.title());
        assertEquals(NoteType.QUOTE, response.noteType());

        ArgumentCaptor<ReadingNote> captor = ArgumentCaptor.forClass(ReadingNote.class);

        verify(readingNoteRepository).save(captor.capture());
        assertEquals(userBook, captor.getValue().getUserBook());
        assertEquals("A memorable passage from the book.", captor.getValue().getContent());
    }

    @Test
    void createNote_shouldThrowException_whenLibraryEntryBelongsToAnotherUser() {
        ReadingNoteRequest request = new ReadingNoteRequest(101L, "Private note", "Private content", NoteType.GENERAL);

        when(userBookRepository.findById(101L)).thenReturn(Optional.of(anotherUserBook));
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.createNote(1L, request));
        verify(readingNoteRepository, never()).save(any(ReadingNote.class));
    }

    @Test
    void createNote_shouldThrowException_whenLibraryEntryDoesNotExist() {
        ReadingNoteRequest request = new ReadingNoteRequest(999L, "Test note", "Test content", NoteType.GENERAL);

        when(userBookRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.createNote(1L, request));
        verify(readingNoteRepository, never()).save(any(ReadingNote.class));
    }

    @Test
    void getNotesForBook_shouldReturnAllNotes() {
        when(userBookRepository.findById(100L)).thenReturn(Optional.of(userBook));
        when(readingNoteRepository.findByUserBookId(100L)).thenReturn(List.of(note));

        List<ReadingNoteResponse> responses = readingNoteService.getNotesForBook(1L, 100L);

        assertEquals(1, responses.size());
        assertEquals(200L, responses.get(0).id());
        assertEquals("My favorite quote", responses.get(0).title());
        assertEquals("A Connecticut Yankee in King Arthur's Court", responses.get(0).bookTitle());
        verify(readingNoteRepository).findByUserBookId(100L);
    }

    @Test
    void getNotesForBook_shouldThrowException_whenLibraryBelongsToAnotherUser() {
        when(userBookRepository.findById(101L)).thenReturn(Optional.of(anotherUserBook));
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.getNotesForBook(1L, 101L));
        verify(readingNoteRepository, never()).findByUserBookId(anyLong());
    }

    @Test
    void getNoteById_shouldReturnNote_whenNoteExistsAndBelongsToUser() {
        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));

        ReadingNoteResponse response = readingNoteService.getNoteById(1L, 200L);

        assertNotNull(response);
        assertEquals(200L, response.id());
        assertEquals("My favorite quote", response.title());
        assertEquals(NoteType.QUOTE, response.noteType());
    }

    @Test
    void getNoteById_shouldThrowException_whenNoteDoesNotExist() {
        when(readingNoteRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.getNoteById(1L, 999L));
    }

    @Test
    void getNoteById_shouldThrowException_whenNoteBelongsToAnotherUser() {
        note.setUserBook(anotherUserBook);

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.getNoteById(1L, 200L));
    }

    @Test
    void updateNote_shouldUpdateAndReturnNote() {
        ReadingNoteRequest request = new ReadingNoteRequest(100L, "Updated title", "Updated content", NoteType.REFLECTION);

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));
        when(userBookRepository.findById(100L)).thenReturn(Optional.of(userBook));

        ReadingNoteResponse response = readingNoteService.updateNote(1L, 200L, request);

        assertEquals("Updated title", response.title());
        assertEquals("Updated content", response.content());
        assertEquals(NoteType.REFLECTION, response.noteType());

        assertEquals("Updated title", note.getTitle());
        assertEquals("Updated content", note.getContent());
        assertEquals(NoteType.REFLECTION, note.getNoteType());
    }

    @Test
    void updateNote_shouldThrowException_whenNoteBelongsToAnotherUser() {
        note.setUserBook(anotherUserBook);

        ReadingNoteRequest request = new ReadingNoteRequest(100L, "Updated title", "Updated content", NoteType.GENERAL);

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.updateNote(1L, 200L, request));
        verify(userBookRepository, never()).findById(anyLong());
    }


    @Test
    void updateNote_shouldThrowException_whenTargetLibraryBelongsToAnotherUser() {
        ReadingNoteRequest request = new ReadingNoteRequest(101L, "Updated title", "Updated content", NoteType.GENERAL);

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));
        when(userBookRepository.findById(101L)).thenReturn(Optional.of(anotherUserBook));
        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.updateNote(1L, 200L, request));
        assertEquals("My favorite quote", note.getTitle());
    }

    @Test
    void deleteNote_shouldDeleteNote_whenNoteBelongsToUser() {

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));

        assertDoesNotThrow(() -> readingNoteService.deleteNote(1L, 200L));

        verify(readingNoteRepository).delete(note);
    }

    @Test
    void deleteNote_shouldThrowException_whenNoteBelongsToAnotherUser() {

        note.setUserBook(anotherUserBook);

        when(readingNoteRepository.findById(200L)).thenReturn(Optional.of(note));

        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.deleteNote(1L, 200L));

        verify(readingNoteRepository, never()).delete(any(ReadingNote.class));
    }

    @Test
    void deleteNote_shouldThrowException_whenNoteDoesNotExist() {

        when(readingNoteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> readingNoteService.deleteNote(1L, 999L));

        verify(readingNoteRepository, never()).delete(any(ReadingNote.class));
    }
}
