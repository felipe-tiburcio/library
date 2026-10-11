package com.ftatech.biblioteca.report;

import java.util.List;

import com.ftatech.biblioteca.loan.LoanService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final LoanService loanService;

    public ReportController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping({ "/borrowed-books/top", "/top-borrowed-books", "/books/top-borrowed" })
    public List<TopBorrowedBookReport> topBorrowedBooks() {
        return loanService.topBorrowedBooks();
    }

    @GetMapping({ "/borrowed-books/current", "/currently-borrowed-books", "/books/current-borrowed" })
    public List<CurrentlyBorrowedBookReport> currentlyBorrowedBooks() {
        return loanService.currentlyBorrowedBooks();
    }
}
