package com.ftatech.library.loan;

import java.time.LocalDate;

import com.ftatech.library.book.Book;
import com.ftatech.library.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate borrowedAt;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnedAt;

    protected Loan() {
    }

    public Loan(Book book, User user, LocalDate borrowedAt, LocalDate dueDate) {
        this.book = book;
        this.user = user;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }

    public boolean isReturned() {
        return returnedAt != null;
    }

    public void returnBook(LocalDate returnedAt) {
        this.returnedAt = returnedAt;
    }
}
