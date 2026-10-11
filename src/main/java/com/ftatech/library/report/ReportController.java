package com.ftatech.library.report;

import java.util.List;

import com.ftatech.library.loan.LoanService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final LoanService loanService;

    public ReportController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "List the most borrowed books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top borrowed books report generated")
    })
    @GetMapping({ "/borrowed-books/top", "/top-borrowed-books", "/books/top-borrowed" })
    public List<TopBorrowedBookReport> topBorrowedBooks() {
        return loanService.topBorrowedBooks();
    }

    @Operation(summary = "List currently borrowed books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Currently borrowed books report generated")
    })
    @GetMapping({ "/borrowed-books/current", "/currently-borrowed-books", "/books/current-borrowed" })
    public List<CurrentlyBorrowedBookReport> currentlyBorrowedBooks() {
        return loanService.currentlyBorrowedBooks();
    }
}
