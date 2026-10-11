package com.ftatech.library.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        List<Book> books = List.of(new Book("Clean Code", "Robert C. Martin", "9780132350884", true));
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.findAll();

        assertEquals(books, result);
        verify(bookRepository).findAll();
    }

    @Test
    void shouldFilterBooksByCriteria() {
        Book matchingBook = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        Book otherBook = new Book("The Pragmatic Programmer", "Andrew Hunt", "9780201616224", false);
        when(bookRepository.findAll()).thenReturn(List.of(matchingBook, otherBook));

        List<Book> result = bookService.findAll(new BookSearchCriteria("clean", "martin", null, true));

        assertEquals(List.of(matchingBook), result);
    }

    @Test
    void shouldCreateBook() {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.create(book);

        assertEquals(book, result);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectBookWithDuplicateIsbn() {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.create(book));

        assertEquals(409, exception.getStatusCode().value());
        verify(bookRepository, org.mockito.Mockito.never()).save(book);
    }

    @Test
    void shouldUpdateBook() {
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
        Book existingBook = new Book("Old title", "Old author", "old-isbn", false);
        Book input = new Book("New title", "New author", "duplicate-isbn", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot("duplicate-isbn", 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.update(1L, input));

        assertEquals(409, exception.getStatusCode().value());
        verify(bookRepository, org.mockito.Mockito.never()).save(existingBook);
    }

    @Test
    void shouldDeleteBook() {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowNotFoundWhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.findById(1L));

        assertEquals(404, exception.getStatusCode().value());
    }
}
