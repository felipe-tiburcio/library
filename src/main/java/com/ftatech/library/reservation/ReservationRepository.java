package com.ftatech.library.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByBookIdAndUserIdAndActiveTrue(Long bookId, Long userId);
}
