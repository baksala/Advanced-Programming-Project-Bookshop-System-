package com.example.Ap.controller;

import com.example.Ap.model.CartItem;
import com.example.Ap.model.Order;
import com.example.Ap.model.User;
import com.example.Ap.service.CartService;
import com.example.Ap.service.OrderService;
import com.example.Ap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    private User resolve(UserDetails p) {
        return userService.findByEmail(p.getUsername());
    }

    /** GET /cart */
    @GetMapping
    public String cartPage(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal == null) return "redirect:/login"; //if not logged in, log in first
        User user = resolve(principal);
        List<CartItem> items = cartService.getCart(user);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            BigDecimal lineTotal = ci.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(lineTotal);
        }
        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        return "cart";
    }

    /** POST /cart/add */
    @PostMapping("/add")
    public String add(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestHeader(value = "Referer", defaultValue = "/") String referer,
            RedirectAttributes ra) {
        if (principal == null) return "redirect:/login"; //if not logged in, log in first
        cartService.addToCart(resolve(principal), bookId, quantity);
        ra.addFlashAttribute("cartMessage", "Added to your cart.");
        return "redirect:" + referer;
    }

    /** POST /cart/remove */
    @PostMapping("/remove")
    public String remove(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam Long bookId) {
        if (principal == null) return "redirect:/login"; //if not logged in, log in first
        cartService.removeFromCart(resolve(principal), bookId);
        return "redirect:/cart";
    }

    /** POST /cart/checkout */
    @PostMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String shippingMethod,
            @RequestParam String shippingAddress,
            RedirectAttributes ra) {
        if (principal == null) return "redirect:/login"; //if not logged in, lig in first
        try {
            Order order = orderService.checkoutCart(resolve(principal), shippingMethod, shippingAddress);
            return "redirect:/cart/confirmation/" + order.getId();
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    /** GET /cart/confirmation/{id} */
    @GetMapping("/confirmation/{id}")
    public String confirmation(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            Model model) {
        if (principal == null) return "redirect:/login";
        Order order = orderService.getOrderById(id);
        User user = resolve(principal);
        if (!order.getUser().getId().equals(user.getId())) return "redirect:/";
        model.addAttribute("order", order);
        return "confirmation";
    }
}
