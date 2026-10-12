package com.ftatech.library.book;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> findAll(BookSearchCriteria criteria, Pageable pageable) {
        if (criteria == null) {
            return findAll(pageable);
        }

        return bookRepository.findAll(buildSpecification(criteria), pageable);
    }

    private Specification<Book> buildSpecification(BookSearchCriteria criteria) {
        Specification<Book> specification = (root, query, builder) -> builder.conjunction();

        if (criteria.title() != null && !criteria.title().isBlank()) {
            specification = specification.and(containsIgnoreCaseField("title", criteria.title()));
        }
        if (criteria.author() != null && !criteria.author().isBlank()) {
            specification = specification.and(containsIgnoreCaseField("author", criteria.author()));
        }
        if (criteria.isbn() != null && !criteria.isbn().isBlank()) {
            specification = specification.and(containsIgnoreCaseField("isbn", criteria.isbn()));
        }
        if (criteria.available() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("available"), criteria.available()));
        }

        return specification;
    }

    private Specification<Book> containsIgnoreCaseField(String field, String value) {
        String pattern = "%" + value.toLowerCase(Locale.ROOT) + "%";
        return (root, query, builder) -> builder.like(
                builder.lower(root.get(field)), pattern);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public Book create(Book book) {
        ensureIsbnIsAvailable(book.getIsbn());
        return bookRepository.save(book);
    }

    public Book update(Long id, Book input) {
        Book book = findById(id);
        ensureIsbnIsAvailable(input.getIsbn(), id);
        book.update(input.getTitle(), input.getAuthor(), input.getIsbn(), input.isAvailable());
        return bookRepository.save(book);
    }

    private void ensureIsbnIsAvailable(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN is already registered");
        }
    }

    private void ensureIsbnIsAvailable(String isbn, Long id) {
        if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN is already registered");
        }
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

}
