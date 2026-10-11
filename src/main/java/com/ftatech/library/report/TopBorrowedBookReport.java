package com.ftatech.library.report;

public record TopBorrowedBookReport(Long bookId, String title, String author, String isbn, long borrowCount) {
}
