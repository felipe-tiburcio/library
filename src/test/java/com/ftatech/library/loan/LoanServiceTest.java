package com.ftatech.library.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.ftatech.library.book.Book;
import com.ftatech.library.book.BookService;
import com.ftatech.library.report.CurrentlyBorrowedBookReport;
import com.ftatech.library.report.TopBorrowedBookReport;
import com.ftatech.library.user.User;
import com.ftatech.library.user.UserType;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @Mock
    private com.ftatech.library.user.UserService userService;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookService, userService);
    }

    @Test
    void shouldCreateLoan() {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", true);
        User user = new User("Ana Silva", "ana.silva@example.com", UserType.STUDENT);
        assignId(book, "id", 1L);
        assignId(user, "id", 2L);

        when(bookService.markAsBorrowed(1L)).thenReturn(book);
        when(userService.findById(2L)).thenReturn(user);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            assignId(loan, "id", 3L);
            return loan;
        });

        LoanResponse response = loanService.borrow(new LoanRequest(1L, 2L, LocalDate.of(2026, 10, 1), null));

        assertEquals(3L, response.id());
        assertEquals(1L, response.bookId());
        assertEquals(2L, response.userId());
        assertEquals(LocalDate.of(2026, 10, 1), response.borrowedAt());
        assertEquals(LocalDate.of(2026, 10, 8), response.dueDate());
        assertNotNull(response);
        verify(bookService).markAsBorrowed(1L);
        verify(userService).findById(2L);
    }

    @Test
    void shouldReturnLoan() {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", false);
        User user = new User("Ana Silva", "ana.silva@example.com", UserType.STUDENT);
        Loan loan = new Loan(book, user, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8));
        assignId(book, "id", 1L);
        assignId(user, "id", 2L);
        assignId(loan, "id", 3L);

        when(loanRepository.findById(3L)).thenReturn(Optional.of(loan));
        when(bookService.markAsReturned(1L)).thenReturn(book);
        when(loanRepository.save(loan)).thenReturn(loan);

        LoanResponse response = loanService.returnLoan(3L);

        assertEquals(LocalDate.now(), response.returnedAt());
        verify(bookService).markAsReturned(1L);
    }

    @Test
    void shouldListAllLoans() {
        Loan loan = createLoan(1L, "Clean Code", "Robert C. Martin", "9780132350884", 2L, "Ana Silva");
        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<LoanResponse> responses = loanService.findAll();

        assertEquals(1, responses.size());
        assertEquals(11L, responses.get(0).id());
        assertEquals(1L, responses.get(0).bookId());
        assertEquals(2L, responses.get(0).userId());
    }

    @Test
    void shouldListTopBorrowedBooks() {
        Loan firstLoan = createLoan(1L, "Clean Code", "Robert C. Martin", "9780132350884", 2L, "Ana Silva");
        Loan secondLoan = createLoan(1L, "Clean Code", "Robert C. Martin", "9780132350884", 3L, "Bruno Costa");
        Loan otherLoan = createLoan(4L, "The Hobbit", "J. R. R. Tolkien", "9780547928227", 2L, "Ana Silva");
        when(loanRepository.findAll()).thenReturn(List.of(firstLoan, secondLoan, otherLoan));

        List<TopBorrowedBookReport> reports = loanService.topBorrowedBooks();

        assertEquals(2, reports.size());
        assertEquals(1L, reports.get(0).bookId());
        assertEquals(2L, reports.get(0).borrowCount());
    }

    @Test
    void shouldListCurrentlyBorrowedBooks() {
        Loan openLoan = createLoan(1L, "Clean Code", "Robert C. Martin", "9780132350884", 2L, "Ana Silva");
        Loan returnedLoan = createLoan(4L, "The Hobbit", "J. R. R. Tolkien", "9780547928227", 3L, "Bruno Costa");
        returnedLoan.returnBook(LocalDate.of(2026, 10, 3));
        when(loanRepository.findAll()).thenReturn(List.of(returnedLoan, openLoan));

        List<CurrentlyBorrowedBookReport> reports = loanService.currentlyBorrowedBooks();

        assertEquals(1, reports.size());
        assertEquals(1L, reports.get(0).bookId());
        assertEquals(LocalDate.of(2026, 10, 8), reports.get(0).dueDate());
    }

    @Test
    void shouldThrowWhenLoanDoesNotExist() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> loanService.findById(1L));

        assertEquals(404, exception.getStatusCode().value());
    }

    private Loan createLoan(Long bookId, String title, String author, String isbn, Long userId, String userName) {
        Book book = new Book(title, author, isbn, true);
        User user = new User(userName, userId + "@example.com", UserType.STUDENT);
        Loan loan = new Loan(book, user, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8));
        assignId(book, "id", bookId);
        assignId(user, "id", userId);
        assignId(loan, "id", bookId + 10);
        return loan;
    }

    private void assignId(Object target, String fieldName, Long value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not set test id", exception);
        }
    }
}
