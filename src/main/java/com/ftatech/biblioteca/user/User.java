package com.ftatech.biblioteca.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import com.ftatech.biblioteca.loan.Loan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Loan> loans = new ArrayList<>();

    protected User() {
    }

    public User(String name, String email, UserType type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserType getType() {
        return type;
    }

    public List<Loan> getLoans() {
        return List.copyOf(loans);
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    public void update(String name, String email, UserType type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }
}
