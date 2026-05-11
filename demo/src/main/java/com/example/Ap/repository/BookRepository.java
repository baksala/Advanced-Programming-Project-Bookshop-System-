package com.example.Ap.repository;

import com.example.Ap.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findFirstByFeaturedTrue();

    List<Book> findTop8ByOrderByAddedDateDesc();
    List<Book> findTop5ByOrderByAddedDateDesc();

    List<Book> findByBadge(String badge);

    @Query("""
        SELECT b FROM Book b
        WHERE (:genres IS NULL OR b.genre IN :genres)
          AND (:minPrice IS NULL OR b.price >= :minPrice)
          AND (:maxPrice IS NULL OR b.price <= :maxPrice)
          AND (:q IS NULL
               OR LOWER(b.title)  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    List<Book> findWithFilters(
        @Param("genres")   List<String> genres,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("q")        String q
    );

    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.genre IS NOT NULL ORDER BY b.genre")
    List<String> findAllGenres();
}
