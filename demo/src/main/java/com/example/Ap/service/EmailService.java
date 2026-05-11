package com.example.Ap.service;

import com.example.Ap.model.Order;
import com.example.Ap.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Bonus: Email Sending + Multithreading (@Async)
 * All methods run on a separate thread from the request thread,
 * so the user gets a fast response while email is sent in the background.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends an order confirmation email asynchronously after checkout.
     * The @Async annotation means this runs on a background thread (Multithreading bonus).
     */
    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            String to      = order.getUser().getEmail();
            String name    = order.getUser().getFirstName();
            String subject = "📦 Order Confirmed – " + order.getOrderNumber();

            StringBuilder body = new StringBuilder();
            body.append("Hi ").append(name).append(",\n\n");
            body.append("Thank you for your order at Bibliotech! 🎉\n\n");
            body.append("─────────────────────────────\n");
            body.append("Order Number : ").append(order.getOrderNumber()).append("\n");
            body.append("Status       : ").append(order.getStatus()).append("\n");
            body.append("Shipping     : ").append(order.getShippingMethod()).append("\n");
            body.append("Address      : ").append(order.getShippingAddress()).append("\n");
            body.append("─────────────────────────────\n\n");
            body.append("Items Ordered:\n");

            for (OrderItem item : order.getItems()) {
                body.append("  • ").append(item.getBook().getTitle())
                    .append(" x").append(item.getQuantity())
                    .append("  ($").append(item.getPriceAtPurchase()).append(" each)\n");
            }

            body.append("\nTotal: $").append(order.getTotalAmount()).append("\n\n");
            body.append("We'll notify you when your order ships.\n\n");
            body.append("Happy reading! 📚\n");
            body.append("— The Bibliotech Team\n");

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body.toString());

            mailSender.send(msg);
            log.info("[EmailService] Order confirmation sent to {} for order {}", to, order.getOrderNumber());

        } catch (Exception e) {
            // Never crash the main thread — just log the failure
            log.error("[EmailService] Failed to send confirmation for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    /**
     * Sends a welcome email when a new user registers.
     * Also runs asynchronously on a background thread.
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("Welcome to Bibliotech! 📚");
            msg.setText(
                "Hi " + firstName + ",\n\n" +
                "Welcome to Bibliotech – your destination for great books!\n\n" +
                "Your account has been created successfully. You can now:\n" +
                "  • Browse our full catalogue\n" +
                "  • Add books to your cart\n" +
                "  • Track your orders\n\n" +
                "Happy reading!\n" +
                "— The Bibliotech Team\n"
            );
            mailSender.send(msg);
            log.info("[EmailService] Welcome email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("[EmailService] Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}
