package com.springbook.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbook.book.dto.BookRequest;
import com.springbook.book.dto.BookResponse;
import com.springbook.book.service.BookService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        BookController controller = new BookController(bookService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        objectMapper = new ObjectMapper().findAndRegisterModules();

        bookResponse = new BookResponse(
                1L,
                "9780141439518",
                "Pride and Prejudice",
                "Jane Austen",
                "A classic novel about love and society.",
                "https://example.com/cover.jpg",
                "Classic",
                1813
        );
    }

    @Test
    void createBook_shouldReturnCreated() throws Exception {
        BookRequest request = new BookRequest(
                "9780141439518",
                "Pride and Prejudice",
                "Jane Austen",
                "A classic novel about love and society.",
                "https://example.com/cover.jpg",
                "Classic",
                1813
        );

        when(bookService.createBook(request)).thenReturn(bookResponse);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("9780141439518"))
                .andExpect(jsonPath("$.title").value("Pride and Prejudice"))
                .andExpect(jsonPath("$.author").value("Jane Austen"))
                .andExpect(jsonPath("$.genre").value("Classic"))
                .andExpect(jsonPath("$.publicationYear").value(1813));
        verify(bookService).createBook(request);
    }

    @Test
    void createBook_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                    "isbn": "",
                    "title": "",
                    "author": "",
                    "description": "Description",
                    "coverUrl": "not-a-valid-url",
                    "genre": "",
                    "publicationYear": -100
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
        verify(bookService, never()).createBook(any(BookRequest.class));
    }

    @Test
    void getAllBooks_shouldReturnBooks() throws Exception {
        when(bookService.getAllBooks())
                .thenReturn(List.of(bookResponse));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Pride and Prejudice"))
                .andExpect(jsonPath("$[0].author").value("Jane Austen"));

        verify(bookService).getAllBooks();
    }

    @Test
    void getAllBooks_shouldReturnEmptyList_whenNoBooksExist()
            throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookService).getAllBooks();
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(bookResponse);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Pride and Prejudice"))
                .andExpect(jsonPath("$.author").value("Jane Austen"));

        verify(bookService).getBookById(1L);
    }

    @Test
    void searchBooks_shouldReturnMatchingBooks() throws Exception {
        when(bookService.searchBooksByTitle("Pride")).thenReturn(List.of(bookResponse));

        mockMvc.perform(get("/api/books/search").param("title", "Pride"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Pride and Prejudice"));

        verify(bookService).searchBooksByTitle("Pride");
    }
}