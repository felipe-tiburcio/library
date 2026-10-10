package com.ftatech.biblioteca.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftatech.biblioteca.user.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
