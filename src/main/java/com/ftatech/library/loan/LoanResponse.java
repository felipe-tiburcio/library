package com.ftatech.library.loan;

import java.time.LocalDate;

public record LoanResponse(Long id, Long bookId, String bookTitle, Long userId, String userName,
        LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {
}
