package com.ftatech.library.book;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldFindAllBooks() {
        setUp();
        List<Book> books = List.of(new Book("Clean Code", "Robert C. Martin", "9780132350884", true));
        when(bookRepository.findAll(PageRequest.of(0, 20))).thenReturn(new PageImpl<>(books));

        Page<Book> result = bookService.findAll(PageRequest.of(0, 20));

        assertEquals(books, result.getContent());
        verify(bookRepository).findAll(PageRequest.of(0, 20));
    }

    @Test
    void shouldFindPaginatedBooks() {
        setUp();
        List<Book> books = List.of(
                new Book("Book 1", "Author 1", "isbn-1", true),
                new Book("Book 2", "Author 2", "isbn-2", true));
        PageRequest pageRequest = PageRequest.of(1, 1);
        when(bookRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(new PageImpl<>(List.of(books.get(1)), pageRequest, 2));

        Page<Book> result = bookService.findAll(new BookSearchCriteria(null, null, null, null), pageRequest);

        assertEquals(List.of(books.get(1)), result.getContent());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void shouldFilterBooksByCriteria() {
        setUp();
        Book matchingBook = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        PageRequest pageRequest = PageRequest.of(0, 20);
        when(bookRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(new PageImpl<>(List.of(matchingBook), pageRequest, 1));

        Page<Book> result = bookService.findAll(new BookSearchCriteria("clean", "martin", null, true),
                pageRequest);

        assertEquals(List.of(matchingBook), result.getContent());
    }

    @Test
    void shouldCreateBook() {
        setUp();
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.create(book);

        assertEquals(book, result);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectBookWithDuplicateIsbn() {
        setUp();
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.create(book));

        assertEquals(409, exception.getStatusCode().value());
        verify(bookRepository, never()).save(book);
    }

    @Test
    void shouldUpdateBook() {
        setUp();
        Book existingBook = new Book("Old title", "Old author", "old-isbn", false);
        Book input = new Book("New title", "New author", "new-isbn", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        Book result = bookService.update(1L, input);

        assertEquals(existingBook, result);
        assertEquals("New title", existingBook.getTitle());
        assertEquals("New author", existingBook.getAuthor());
        assertEquals("new-isbn", existingBook.getIsbn());
        assertEquals(true, existingBook.isAvailable());
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldRejectUpdateWhenIsbnBelongsToAnotherBook() {
        setUp();
        Book existingBook = new Book("Old title", "Old author", "old-isbn", false);
        Book input = new Book("New title", "New author", "duplicate-isbn", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot("duplicate-isbn", 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.update(1L, input));

        assertEquals(409, exception.getStatusCode().value());
        verify(bookRepository, never()).save(existingBook);
    }

    @Test
    void shouldDeleteBook() {
        setUp();
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowNotFoundWhenBookDoesNotExist() {
        setUp();
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.findById(1L));

        assertEquals(404, exception.getStatusCode().value());
    }
}
