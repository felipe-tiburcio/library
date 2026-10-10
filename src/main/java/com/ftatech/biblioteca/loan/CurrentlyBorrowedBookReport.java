package com.ftatech.biblioteca.loan;

import java.time.LocalDate;

public record CurrentlyBorrowedBookReport(Long loanId, Long bookId, String bookTitle, Long userId, String userName,
        LocalDate borrowedAt, LocalDate dueDate) {
}
