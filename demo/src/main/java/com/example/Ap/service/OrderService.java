package com.example.Ap.service;

import com.example.Ap.model.*;
import com.example.Ap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService; // Bonus: Email + Multithreading

    /** user's order history — items + books eagerly fetched, no lazy exceptions */
    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserWithItems(user);
    }

    /** single order with items + books eagerly fetched */
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    /** all orders with user + items + books — for admin panel */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    @Transactional
    public Order checkoutCart(User user, String shippingMethod, String shippingAddress) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty.");

        BigDecimal shipping = switch (shippingMethod) {
            case "Express"   -> new BigDecimal("9.99");
            case "Overnight" -> new BigDecimal("19.99");
            default          -> new BigDecimal("4.99");  // Standard
        };

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            BigDecimal lineTotal = ci.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal total = subtotal.add(shipping);

        Order order = Order.builder()
                .orderNumber("BLT-" + UUID.randomUUID().toString().substring(0, 6))
                .user(user)
                .status(Order.Status.PENDING)
                .shippingMethod(shippingMethod)
                .shippingAddress(shippingAddress)
                .totalAmount(total)
                .createdAt(LocalDateTime.now())
                .build();

        for (CartItem ci : cartItems) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .book(ci.getBook())
                    .quantity(ci.getQuantity())
                    .priceAtPurchase(ci.getBook().getPrice())
                    .build();
            order.getItems().add(oi);
            Book book = ci.getBook();
            book.setStockQuantity(Math.max(0, book.getStockQuantity() - ci.getQuantity()));
            bookRepository.save(book);
        }

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByUserId(user.getId());

        // bonus: send order confirmation email asynchronously (Multithreading via @Async)
        emailService.sendOrderConfirmation(saved);

        return saved;
    }

    @Transactional
    public void updateStatus(Long orderId, Order.Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    public BigDecimal getTotalSales() {
        BigDecimal result = orderRepository.sumTotalSales();
        return result != null ? result : BigDecimal.ZERO;
    }

    public long countNewOrders() {
        return orderRepository.countByStatus(Order.Status.PENDING)
             + orderRepository.countByStatus(Order.Status.PROCESSING);
    }
}
