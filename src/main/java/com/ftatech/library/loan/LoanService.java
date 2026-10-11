package com.ftatech.library.loan;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ftatech.library.book.Book;
import com.ftatech.library.book.BookService;
import com.ftatech.library.report.CurrentlyBorrowedBookReport;
import com.ftatech.library.report.TopBorrowedBookReport;
import com.ftatech.library.user.User;
import com.ftatech.library.user.UserService;

@Service
public class LoanService {

    private static final int DEFAULT_LOAN_DAYS = 7;
    private static final int TOP_BORROWED_BOOKS_LIMIT = 20;

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;

    public LoanService(LoanRepository loanRepository, BookService bookService, UserService userService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    public LoanResponse borrow(LoanRequest request) {
        validateRequest(request);

        Book book = bookService.markAsBorrowed(request.bookId());
        User user = userService.findById(request.userId());
        LocalDate borrowedAt = request.borrowedAt() != null ? request.borrowedAt() : LocalDate.now();
        LocalDate dueDate = request.dueDate() != null ? request.dueDate() : borrowedAt.plusDays(DEFAULT_LOAN_DAYS);

        Loan loan = new Loan(book, user, borrowedAt, dueDate);
        Loan savedLoan = loanRepository.save(loan);
        user.addLoan(savedLoan);

        return toResponse(savedLoan);
    }

    @Transactional
    public LoanResponse returnLoan(Long id) {
        Loan loan = findLoan(id);
        if (loan.isReturned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Loan has already been returned");
        }

        loan.returnBook(LocalDate.now());
        bookService.markAsReturned(loan.getBook().getId());
        return toResponse(loanRepository.save(loan));
    }

    public LoanResponse findById(Long id) {
        return toResponse(findLoan(id));
    }

    public List<TopBorrowedBookReport> topBorrowedBooks() {
        Map<Long, List<Loan>> loansByBookId = loanRepository.findAll().stream()
                .collect(Collectors.groupingBy(loan -> loan.getBook().getId()));

        return loansByBookId.entrySet().stream()
                .map(this::toTopBorrowedBookReport)
                .sorted(Comparator.comparingLong(TopBorrowedBookReport::borrowCount).reversed()
                        .thenComparing(TopBorrowedBookReport::title))
                .limit(TOP_BORROWED_BOOKS_LIMIT)
                .toList();
    }

    public List<CurrentlyBorrowedBookReport> currentlyBorrowedBooks() {
        return loanRepository.findAll().stream()
                .filter(loan -> !loan.isReturned())
                .sorted(Comparator.comparing(Loan::getDueDate).thenComparing(Loan::getBorrowedAt))
                .map(this::toCurrentlyBorrowedBookReport)
                .toList();
    }

    private Loan findLoan(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    private void validateRequest(LoanRequest request) {
        if (request == null || request.bookId() == null || request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book and user are required");
        }
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getUser().getId(),
                loan.getUser().getName(),
                loan.getBorrowedAt(),
                loan.getDueDate(),
                loan.getReturnedAt());
    }

    private TopBorrowedBookReport toTopBorrowedBookReport(Map.Entry<Long, List<Loan>> entry) {
        Loan loan = entry.getValue().getFirst();
        return new TopBorrowedBookReport(
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBook().getAuthor(),
                loan.getBook().getIsbn(),
                entry.getValue().size());
    }

    private CurrentlyBorrowedBookReport toCurrentlyBorrowedBookReport(Loan loan) {
        return new CurrentlyBorrowedBookReport(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getUser().getId(),
                loan.getUser().getName(),
                loan.getBorrowedAt(),
                loan.getDueDate());
    }
}
