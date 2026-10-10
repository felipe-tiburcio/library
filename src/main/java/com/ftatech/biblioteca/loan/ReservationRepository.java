package com.ftatech.biblioteca.loan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByBookIdAndUserIdAndActiveTrue(Long bookId, Long userId);
}
