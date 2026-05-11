package com.example.Ap.repository;

import com.example.Ap.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Native SQL skips Hibernate's entity cache — safe to call before book deletion
    @Modifying
    @Query(value = "DELETE FROM order_items WHERE book_id = :bookId", nativeQuery = true)
    void deleteByBookId(@Param("bookId") Long bookId);
}
