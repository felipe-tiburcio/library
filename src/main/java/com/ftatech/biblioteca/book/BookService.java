package com.ftatech.biblioteca.book;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findAll(BookSearchCriteria criteria) {
        if (criteria == null) {
            return findAll();
        }

        return bookRepository.findAll().stream()
                .filter(book -> matchesTitle(book, criteria.title()))
                .filter(book -> matchesAuthor(book, criteria.author()))
                .filter(book -> matchesIsbn(book, criteria.isbn()))
                .filter(book -> matchesAvailability(book, criteria.available()))
                .toList();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book input) {
        Book book = findById(id);
        book.update(input.getTitle(), input.getAuthor(), input.getIsbn(), input.isAvailable());
        return bookRepository.save(book);
    }

    public Book markAsBorrowed(Long id) {
        Book book = findById(id);
        if (!book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book is not available");
        }

        book.markAsBorrowed();
        return bookRepository.save(book);
    }

    public Book markAsReturned(Long id) {
        Book book = findById(id);
        book.markAsReturned();
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    private boolean matchesTitle(Book book, String title) {
        if (title == null || title.isBlank()) {
            return true;
        }

        return containsIgnoreCase(book.getTitle(), title);
    }

    private boolean matchesAuthor(Book book, String author) {
        if (author == null || author.isBlank()) {
            return true;
        }

        return containsIgnoreCase(book.getAuthor(), author);
    }

    private boolean matchesIsbn(Book book, String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return true;
        }

        return containsIgnoreCase(book.getIsbn(), isbn);
    }

    private boolean matchesAvailability(Book book, Boolean available) {
        if (available == null) {
            return true;
        }

        return book.isAvailable() == available;
    }

    private boolean containsIgnoreCase(String value, String filter) {
        return value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }
}
