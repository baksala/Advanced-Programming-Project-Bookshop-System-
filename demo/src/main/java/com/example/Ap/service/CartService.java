package com.example.Ap.service;

import com.example.Ap.model.*;
import com.example.Ap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public void addToCart(User user, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        Optional<CartItem> existing = cartItemRepository.findByUserAndBookId(user, bookId);
        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + quantity);
            cartItemRepository.save(ci);
        } else {
            cartItemRepository.save(CartItem.builder()
                    .user(user).book(book).quantity(quantity).build());
        }
    }

    @Transactional
    public void removeFromCart(User user, Long bookId) {
        cartItemRepository.findByUserAndBookId(user, bookId)
                .ifPresent(cartItemRepository::delete);
    }


    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUserId(user.getId());
    }
}
