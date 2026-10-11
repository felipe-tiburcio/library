package com.ftatech.library.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldFindAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(
                new Book("Clean Code", "Robert C. Martin", "9780132350884", true)));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"))
                .andExpect(jsonPath("$[0].isbn").value("9780132350884"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void shouldFilterBooksByQueryParameters() throws Exception {
        when(bookService.findAll(any(BookSearchCriteria.class))).thenReturn(List.of(
                new Book("Clean Code", "Robert C. Martin", "9780132350884", true)));

        mockMvc.perform(get("/api/books")
                        .param("title", "clean")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookService.create(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert C. Martin",
                                "isbn":"9780132350884","available":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void shouldRejectInvalidQueryParameter() throws Exception {
        mockMvc.perform(get("/api/books").param("available", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request parameter"))
                .andExpect(jsonPath("$.detail").value("Invalid value for parameter 'available'"));
    }

    @Test
    void shouldRejectInvalidBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"Author\",\"isbn\":\"ISBN\",\"available\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookService.findById(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenIsbnIsAlreadyRegistered() throws Exception {
        when(bookService.create(any(Book.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "ISBN is already registered"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"isbn\":\"9780132350884\",\"available\":true}"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookService.update(any(Long.class), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert C. Martin",
                                "isbn":"9780132350884","available":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).delete(1L);
    }
}
