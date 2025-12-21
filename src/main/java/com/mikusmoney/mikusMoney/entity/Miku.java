package com.mikusmoney.mikusMoney.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mikus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Miku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "public_code", nullable = false, unique = true, length = 20)
    private String publicCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One-to-one relationship with Credential
    @OneToOne(mappedBy = "miku", cascade = CascadeType.ALL, orphanRemoval = true)
    private Credential credential;

    // One-to-one relationship with Account
    @OneToOne(mappedBy = "miku", cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    // One-to-one relationship with SavingsPig
    @OneToOne(mappedBy = "miku", cascade = CascadeType.ALL, orphanRemoval = true)
    private SavingsPig savingsPig;

    // Lifecycle callback - executed before persist
    @PrePersist
    protected void onCreate() {
        if (this.publicCode == null) {
            this.publicCode = generatePublicCode();
        }
    }

    // Helper method to get full name
    public String getFullName() {
        return "Miku " + name + " " + lastName;
    }

    // Generate a unique public code (this is a basic implementation, should be enhanced in service)
    public String generatePublicCode() {
        return "MK" + System.currentTimeMillis();
    }

    // Business method to check if Miku is adult (18+)
    public boolean isAdult() {
        return birthDate != null && birthDate.isBefore(LocalDate.now().minusYears(18));
    }

    // Business method to get age
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }
}
