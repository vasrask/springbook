package com.springbook.reading.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbook.reading.ReadingStatus;
import com.springbook.reading.dto.UserBookRequest;
import com.springbook.reading.dto.UserBookResponse;
import com.springbook.reading.service.UserBookService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserBookControllerTest {

    @Mock
    private UserBookService userBookService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserBookResponse userBookResponse;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        UserBookController controller = new UserBookController(userBookService, currentUserService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        objectMapper = new ObjectMapper().findAndRegisterModules();

        userBookResponse = new UserBookResponse(
                100L,
                10L,
                "Pride and Prejudice",
                "Jane Austen",
                ReadingStatus.CURRENTLY_READING,
                5,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void addBook_shouldReturnCreated() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        UserBookRequest request = new UserBookRequest(
                10L,
                ReadingStatus.CURRENTLY_READING,
                5,
                null,
                null,
                null,
                null
        );

        when(userBookService.addBookToLibrary(USER_ID, request)).thenReturn(userBookResponse);

        mockMvc.perform(post("/api/library")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.bookId").value(10))
                .andExpect(jsonPath("$.bookTitle")
                        .value("Pride and Prejudice"))
                .andExpect(jsonPath("$.status")
                        .value("CURRENTLY_READING"))
                .andExpect(jsonPath("$.rating").value(5));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(userBookService).addBookToLibrary(USER_ID, request);
    }

    @Test
    void addBook_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                    "bookId": null,
                    "status": null,
                    "rating": 10,
                    "startedAt": null,
                    "finishedAt": null,
                    "personalSummary": null,
                    "personalReview": null
                }
                """;

        mockMvc.perform(post("/api/library")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(currentUserService, never()).getCurrentUserId(any());
        verify(userBookService, never()).addBookToLibrary(anyLong(), any(UserBookRequest.class));
    }

    @Test
    void getLibrary_shouldReturnUserBooks() throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);

        when(userBookService.getUserLibrary(USER_ID)).thenReturn(List.of(userBookResponse));

        mockMvc.perform(get("/api/library")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].bookTitle")
                        .value("Pride and Prejudice"))
                .andExpect(jsonPath("$[0].status")
                        .value("CURRENTLY_READING"));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(userBookService).getUserLibrary(USER_ID);
    }

    @Test
    void getLibrary_shouldReturnEmptyList_whenLibraryIsEmpty()
            throws Exception {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);
        when(userBookService.getUserLibrary(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/library").principal(authentication)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userBookService).getUserLibrary(USER_ID);
    }

    @Test
    void getBooksByStatus_shouldReturnMatchingBooks() throws Exception {

        when(currentUserService.getCurrentUserId(authentication)).thenReturn(USER_ID);
        when(userBookService.getUserBooksByStatus(USER_ID, ReadingStatus.CURRENTLY_READING))
                .thenReturn(List.of(userBookResponse));

        mockMvc.perform(get("/api/library/status/CURRENTLY_READING").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Pride and Prejudice"))
                .andExpect(jsonPath("$[0].status").value("CURRENTLY_READING"));

        verify(currentUserService).getCurrentUserId(authentication);
        verify(userBookService).getUserBooksByStatus(USER_ID, ReadingStatus.CURRENTLY_READING);
    }

    @Test
    void getBooksByStatus_shouldReturnBadRequest_whenStatusIsInvalid()
            throws Exception {
        mockMvc.perform(get("/api/library/status/INVALID_STATUS").principal(authentication))
                .andExpect(status().isBadRequest());

        verify(currentUserService, never()).getCurrentUserId(any());
        verify(userBookService, never()).getUserBooksByStatus(anyLong(), any());
    }
}