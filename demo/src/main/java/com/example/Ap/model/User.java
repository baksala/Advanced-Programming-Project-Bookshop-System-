package com.example.Ap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 1000)
    private String bio;

    @Column(length = 500)
    private String avatarUrl;

    // @Builder.Default keeps this value when using User.builder()...build()
    @Builder.Default
    private boolean newsletter = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate memberSince = LocalDate.now();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public enum Role { USER, ADMIN }
}
