package com.ftatech.biblioteca.loan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrow(@RequestBody LoanRequest request) {
        return loanService.borrow(request);
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    @GetMapping("/{id}")
    public LoanResponse findById(@PathVariable Long id) {
        return loanService.findById(id);
    }
}
