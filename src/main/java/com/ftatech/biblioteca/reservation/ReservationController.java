package com.ftatech.biblioteca.reservation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation reserve(@RequestBody ReservationRequest request) {
        return reservationService.reserve(request);
    }

    @GetMapping
    public List<Reservation> findAll() {
        return reservationService.findAll();
    }

    @DeleteMapping("/{id}")
    public Reservation cancel(@PathVariable Long id) {
        return reservationService.cancel(id);
    }
}
