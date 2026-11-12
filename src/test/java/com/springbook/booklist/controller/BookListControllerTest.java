package com.springbook.booklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbook.booklist.dto.BookListItemRequest;
import com.springbook.booklist.dto.BookListItemResponse;
import com.springbook.booklist.dto.BookListRequest;
import com.springbook.booklist.dto.BookListResponse;
import com.springbook.booklist.service.BookListService;
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
class BookListControllerTest {

    @Mock
    private BookListService bookListService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final Long USER_ID = 1L;
    private static final Long LIST_ID = 10L;
    private static final Long USER_BOOK_ID = 100L;

    private BookListResponse bookListResponse;
    private BookListItemResponse bookListItemResponse;

    @BeforeEach
    void setUp() {
        BookListController controller = new BookListController(bookListService, currentUserService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper().findAndRegisterModules();

        bookListResponse = new BookListResponse(
                LIST_ID,
                "Favorite Books",
                "My favorite books collection",
                1,
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-01T10:00:00Z")
        );

        bookListItemResponse = new BookListItemResponse(
                200L,
                USER_BOOK_ID,
                20L,
                "Pride and Prejudice",
                "Jane Austen",
                "https://example.com/cover.jpg",
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }

    @Test
    void createBookList_shouldReturnCreated() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        BookListRequest request = new BookListRequest("Favorite Books", "My favorite books collection"
        );

        when(bookListService.createBookList(USER_ID, request)).thenReturn(bookListResponse);

        mockMvc.perform(post("/api/lists")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Favorite Books"))
                .andExpect(jsonPath("$.description").value("My favorite books collection"))
                .andExpect(jsonPath("$.bookCount").value(1));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(bookListService).createBookList(USER_ID, request);
    }

    @Test
    void createBookList_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                    "name": "",
                    "description": "Invalid list"
                }
                """;

        mockMvc.perform(post("/api/lists")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(currentUserService, never()).getCurrentUserId(any());
        verify(bookListService, never()).createBookList(anyLong(), any(BookListRequest.class));
    }

    @Test
    void getAllBookLists_shouldReturnLists() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);
        when(bookListService.getAllBookLists(USER_ID)).thenReturn(List.of(bookListResponse));

        mockMvc.perform(get("/api/lists").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Favorite Books"));

        verify(bookListService).getAllBookLists(USER_ID);
    }

    @Test
    void getBookListById_shouldReturnList() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);
        when(bookListService.getBookListById(USER_ID, LIST_ID)).thenReturn(bookListResponse);

        mockMvc.perform(get("/api/lists/10").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Favorite Books"))
                .andExpect(jsonPath("$.bookCount").value(1));

        verify(bookListService).getBookListById(USER_ID, LIST_ID);
    }

    @Test
    void updateBookList_shouldReturnUpdatedList() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        BookListRequest request = new BookListRequest("Updated Books", "Updated description");

        BookListResponse updatedResponse = new BookListResponse(
                LIST_ID,
                "Updated Books",
                "Updated description",
                1,
                bookListResponse.createdAt(),
                Instant.parse("2026-01-02T10:00:00Z")
        );

        when(bookListService.updateBookList(USER_ID, LIST_ID, request)).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/lists/10")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Updated Books"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        verify(bookListService).updateBookList(USER_ID, LIST_ID, request);
    }

    @Test
    void deleteBookList_shouldReturnNoContent() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        mockMvc.perform(delete("/api/lists/10").principal(authentication))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(bookListService).deleteBookList(USER_ID, LIST_ID);
    }

    @Test
    void addBookToList_shouldReturnCreated() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        BookListItemRequest request = new BookListItemRequest(USER_BOOK_ID);

        when(bookListService.addBookToList(USER_ID, LIST_ID, USER_BOOK_ID)).thenReturn(bookListItemResponse);

        mockMvc.perform(post("/api/lists/10/books")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.userBookId").value(100))
                .andExpect(jsonPath("$.bookId").value(20))
                .andExpect(jsonPath("$.bookTitle").value("Pride and Prejudice"))
                .andExpect(jsonPath("$.bookAuthor").value("Jane Austen"));

        verify(bookListService).addBookToList(USER_ID, LIST_ID, USER_BOOK_ID);
    }

    @Test
    void getBooksInList_shouldReturnBooks() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        when(bookListService.getBooksInList(USER_ID, LIST_ID)).thenReturn(List.of(bookListItemResponse));

        mockMvc.perform(get("/api/lists/10/books").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userBookId").value(100))
                .andExpect(jsonPath("$[0].bookTitle").value("Pride and Prejudice"));

        verify(bookListService).getBooksInList(USER_ID, LIST_ID);
    }

    @Test
    void removeBookFromList_shouldReturnNoContent() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        mockMvc.perform(delete("/api/lists/10/books/100").principal(authentication))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(bookListService).removeBookFromList(USER_ID, LIST_ID, USER_BOOK_ID);
    }
}