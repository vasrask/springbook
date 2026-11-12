package com.springbook.reading.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbook.reading.NoteType;
import com.springbook.reading.dto.ReadingNoteRequest;
import com.springbook.reading.dto.ReadingNoteResponse;
import com.springbook.reading.service.ReadingNoteService;
import com.springbook.security.CurrentUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReadingNoteControllerTest {

    @Mock
    private ReadingNoteService readingNoteService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ReadingNoteResponse noteResponse;

    @BeforeEach
    void setUp() {
        ReadingNoteController controller = new ReadingNoteController(readingNoteService, currentUserService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        objectMapper = new ObjectMapper().findAndRegisterModules();

        noteResponse = new ReadingNoteResponse(
                200L,
                100L,
                "The Adventures of Tom Sawyer",
                "Favorite quote",
                "A memorable passage from the book.",
                NoteType.QUOTE,
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }

    @Test
    void createNote_shouldReturnCreated() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);

        ReadingNoteRequest request = new ReadingNoteRequest(
                100L,
                "Favorite quote",
                "A memorable passage from the book.",
                NoteType.QUOTE
        );

        when(readingNoteService.createNote(1L, request)).thenReturn(noteResponse);

        mockMvc.perform(post("/api/notes")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.userBookId").value(100))
                .andExpect(jsonPath("$.bookTitle")
                        .value("The Adventures of Tom Sawyer"))
                .andExpect(jsonPath("$.title")
                        .value("Favorite quote"))
                .andExpect(jsonPath("$.noteType").value("QUOTE"));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(readingNoteService).createNote(1L, request);
    }

    @Test
    void getNotesForBook_shouldReturnNotes() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);

        when(readingNoteService.getNotesForBook(1L, 100L)).thenReturn(List.of(noteResponse));

        mockMvc.perform(get("/api/notes/book/100")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(200))
                .andExpect(jsonPath("$[0].title")
                        .value("Favorite quote"))
                .andExpect(jsonPath("$[0].noteType").value("QUOTE"));

        verify(readingNoteService).getNotesForBook(1L, 100L);
    }

    @Test
    void getNoteById_shouldReturnNote() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);

        when(readingNoteService.getNoteById(1L, 200L)).thenReturn(noteResponse);

        mockMvc.perform(get("/api/notes/200")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.userBookId").value(100))
                .andExpect(jsonPath("$.title")
                        .value("Favorite quote"))
                .andExpect(jsonPath("$.content")
                        .value("A memorable passage from the book."));

        verify(readingNoteService).getNoteById(1L, 200L);
    }

    @Test
    void updateNote_shouldReturnUpdatedNote() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);

        ReadingNoteRequest request = new ReadingNoteRequest(
                100L,
                "Updated title",
                "Updated note content.",
                NoteType.REFLECTION
        );

        ReadingNoteResponse updatedResponse = new ReadingNoteResponse(
                        200L,
                        100L,
                        "The Adventures of Tom Sawyer",
                        "Updated title",
                        "Updated note content.",
                        NoteType.REFLECTION,
                        noteResponse.createdAt(),
                        Instant.parse("2026-01-02T10:00:00Z")
                );

        when(readingNoteService.updateNote(1L, 200L, request)).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/notes/200")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content")
                        .value("Updated note content."))
                .andExpect(jsonPath("$.noteType")
                        .value("REFLECTION"));

        verify(readingNoteService).updateNote(1L, 200L, request);
    }

    @Test
    void deleteNote_shouldReturnNoContent() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);

        mockMvc.perform(delete("/api/notes/200").principal(authentication))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(currentUserService).getCurrentUserId(authentication);

        verify(readingNoteService).deleteNote(1L, 200L);
    }

    @Test
    void createNote_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {

        String invalidRequest = """
                {
                    "userBookId": null,
                    "title": "",
                    "content": "",
                    "noteType": null
                }
                """;

        mockMvc.perform(post("/api/notes").principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                        .andExpect(status().isBadRequest());

        verify(readingNoteService, never()).createNote(anyLong(), any(ReadingNoteRequest.class));
    }
}