package com.ftatech.biblioteca.loan;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ftatech.biblioteca.book.Book;
import com.ftatech.biblioteca.book.BookService;
import com.ftatech.biblioteca.user.User;
import com.ftatech.biblioteca.user.UserService;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final UserService userService;

    public ReservationService(ReservationRepository reservationRepository, BookService bookService,
            UserService userService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    public Reservation reserve(ReservationRequest request) {
        if (request == null || request.bookId() == null || request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book and user are required");
        }

        Book book = bookService.findById(request.bookId());
        User user = userService.findById(request.userId());
        if (book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only unavailable books can be reserved");
        }
        if (reservationRepository.existsByBookIdAndUserIdAndActiveTrue(book.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book is already reserved by this user");
        }

        return reservationRepository.save(new Reservation(book, user));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        if (!reservation.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation has already been cancelled");
        }
        reservation.cancel();
        return reservationRepository.save(reservation);
    }
}
