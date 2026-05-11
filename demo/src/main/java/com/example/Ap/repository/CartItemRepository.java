package com.example.Ap.repository;

import com.example.Ap.model.CartItem;
import com.example.Ap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndBookId(User user, Long bookId);

    @Modifying
    @Query(value = "DELETE FROM cart_items WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);

    // Native SQL skips Hibernate's entity cache — safe to call before book deletion
    @Modifying
    @Query(value = "DELETE FROM cart_items WHERE book_id = :bookId", nativeQuery = true)
    void deleteByBookId(@Param("bookId") Long bookId);

    default void deleteByUser(User user) {
        deleteByUserId(user.getId());
    }
}
