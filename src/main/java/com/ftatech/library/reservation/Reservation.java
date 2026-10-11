package com.ftatech.library.reservation;

import java.time.LocalDateTime;

import com.ftatech.library.book.Book;
import com.ftatech.library.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime reservedAt;
    private boolean active;

    protected Reservation() {
    }

    public Reservation(Book book, User user) {
        this.book = book;
        this.user = user;
        this.reservedAt = LocalDateTime.now();
        this.active = true;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public boolean isActive() { return active; }

    public void cancel() { this.active = false; }
}
