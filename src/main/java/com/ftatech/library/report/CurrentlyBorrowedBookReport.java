package com.ftatech.library.report;

import java.time.LocalDate;

public record CurrentlyBorrowedBookReport(Long loanId, Long bookId, String bookTitle, Long userId, String userName,
        LocalDate borrowedAt, LocalDate dueDate) {
}
