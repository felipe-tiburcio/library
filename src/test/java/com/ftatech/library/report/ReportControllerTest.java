package com.ftatech.library.report;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ftatech.library.loan.LoanService;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldListTopBorrowedBooks() throws Exception {
        when(loanService.topBorrowedBooks()).thenReturn(List.of(
                new TopBorrowedBookReport(1L, "Clean Code", "Robert C. Martin", "9780132350884", 3L)));

        mockMvc.perform(get("/api/reports/borrowed-books/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].borrowCount").value(3));
    }

    @Test
    void shouldListCurrentlyBorrowedBooks() throws Exception {
        when(loanService.currentlyBorrowedBooks()).thenReturn(List.of(
                new CurrentlyBorrowedBookReport(10L, 1L, "Clean Code", 2L, "Ana Silva",
                        LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8))));

        mockMvc.perform(get("/api/reports/borrowed-books/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dueDate").value("2026-10-08"));
    }
}
