package com.example.Ap.repository;

import com.example.Ap.model.Order;
import com.example.Ap.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Eagerly fetch items + book in one query — prevents LazyInitializationException in templates
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.book
        WHERE o.user = :user
        ORDER BY o.createdAt DESC
        """)
    List<Order> findByUserWithItems(@Param("user") User user);

    // For admin orders page — also fetch user, items and book eagerly
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.user
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.book
        ORDER BY o.createdAt DESC
        """)
    List<Order> findAllWithDetails();

    // Single order with full details for order-detail page
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.book
        WHERE o.id = :id
        """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    List<Order> findAll(Sort sort);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal sumTotalSales();

    long countByStatus(Order.Status status);
}
