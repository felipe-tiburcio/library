package com.ftatech.library.loan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldBorrowBook() throws Exception {
        when(loanService.borrow(any(LoanRequest.class))).thenReturn(new LoanResponse(1L, 2L, "Clean Code", 3L,
                "Ana Silva", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8), null));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":2,"userId":3,"borrowedAt":"2026-10-01","dueDate":"2026-10-08"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Clean Code"));
    }

    @Test
    void shouldReturnBook() throws Exception {
        when(loanService.returnLoan(1L)).thenReturn(new LoanResponse(1L, 2L, "Clean Code", 3L, "Ana Silva",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 10)));

        mockMvc.perform(post("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedAt").value("2026-10-10"));
    }

    @Test
    void shouldFindLoanById() throws Exception {
        when(loanService.findById(1L)).thenReturn(new LoanResponse(1L, 2L, "Clean Code", 3L, "Ana Silva",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8), null));

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
