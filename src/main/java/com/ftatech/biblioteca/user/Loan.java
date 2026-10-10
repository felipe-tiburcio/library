package com.ftatech.biblioteca.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookTitle;
    private LocalDate borrowedAt;
    private LocalDate returnedAt;

    @JsonIgnore
    @ManyToOne
    private User user;

    protected Loan() { }

    public Loan(String bookTitle, LocalDate borrowedAt, LocalDate returnedAt, User user) {
        this.bookTitle = bookTitle;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getBorrowedAt() { return borrowedAt; }
    public LocalDate getReturnedAt() { return returnedAt; }
}
