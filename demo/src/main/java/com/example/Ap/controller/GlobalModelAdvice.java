package com.example.Ap.controller;

import com.example.Ap.model.CartItem;
import com.example.Ap.model.User;
import com.example.Ap.service.CartService;
import com.example.Ap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * injects cart item count into every model — powers the nav badge site-wide.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CartService cartService;
    private final UserService userService;

    @ModelAttribute("cartCount")
    public int cartCount(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return 0;
        try {
            User user = userService.findByEmail(principal.getUsername());
            List<CartItem> items = cartService.getCart(user);
            int total = 0;
            for (CartItem item : items) {
                total += item.getQuantity();
            }
            return total;        } catch (Exception e) {
            return 0;
        }
    }
}
