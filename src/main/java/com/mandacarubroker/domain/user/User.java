package com.mandacarubroker.domain.user;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;

@Table(name = "users")
@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private double balance;

    public User(final RequestUserDTO requestUserDTO) {
        this.email = requestUserDTO.email();
        this.username = requestUserDTO.username();
        this.password = requestUserDTO.password();
        this.firstName = requestUserDTO.firstName();
        this.lastName = requestUserDTO.lastName();
        this.birthDate = requestUserDTO.birthDate();
        this.balance = requestUserDTO.balance();
    }

    public void deposit(final double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    public void withdraw(final double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal.");
        }
        this.balance -= amount;
    }

    @Column(name = "role", columnDefinition = "VARCHAR DEFAULT 'USER'")
    @Enumerated(EnumType.STRING)
    private Role role;

    private Role getRole() {
        if (role == null) {
            role = Role.USER;
        }

        return role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRole().getAuthorities();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
