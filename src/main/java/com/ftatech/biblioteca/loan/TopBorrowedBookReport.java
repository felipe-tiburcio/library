package com.ftatech.biblioteca.loan;

public record TopBorrowedBookReport(Long bookId, String title, String author, String isbn, long borrowCount) {
}
