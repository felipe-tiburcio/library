package com.ftatech.library.loan;

import java.time.LocalDate;

public record LoanRequest(Long bookId, Long userId, LocalDate borrowedAt, LocalDate dueDate) {
}
