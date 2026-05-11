package com.example.Ap.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 300)
    private String author;

    @Column(length = 20)
    private String isbn;

    @Column(length = 100)
    private String genre;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(length = 1000)
    private String coverImageUrl;

    @Column(length = 50)
    private String badge;

    @Builder.Default
    private boolean featured = false;

    @Builder.Default
    @Column(nullable = false)
    private LocalDate addedDate = LocalDate.now();
}
