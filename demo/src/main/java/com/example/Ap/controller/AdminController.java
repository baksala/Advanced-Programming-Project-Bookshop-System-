package com.example.Ap.controller;

import com.example.Ap.dto.BookCreateRequest;
import com.example.Ap.dto.DashboardStats;
import com.example.Ap.model.Book;
import com.example.Ap.model.Order;
import com.example.Ap.service.BookService;
import com.example.Ap.service.OrderService;
import com.example.Ap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;

    /* ── Dashboard ────────────────────────────────────────────────────── */

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("stats", DashboardStats.builder()
                .totalSales(orderService.getTotalSales())
                .totalBooks(bookService.countAll())
                .newOrders(orderService.countNewOrders())
                .build());
        model.addAttribute("bookForm",    new BookCreateRequest());
        model.addAttribute("recentBooks", bookService.getRecentBooks());
        return "Dashboard";
    }

    /* ── Quick Add Book (dashboard form POST) ─────────────────────────── */

    @PostMapping("/books")
    public String addBook(
            @Valid @ModelAttribute("bookForm") BookCreateRequest form,
            BindingResult result,
            @RequestParam(value = "coverImage",    required = false) MultipartFile coverImage,
            @RequestParam(value = "coverImageUrl", required = false) String manualUrl,
            RedirectAttributes ra,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("stats", DashboardStats.builder()
                    .totalSales(orderService.getTotalSales())
                    .totalBooks(bookService.countAll()).newOrders(orderService.countNewOrders()).build());
            model.addAttribute("recentBooks", bookService.getRecentBooks());
            return "Dashboard";
        }

        String imageUrl = (manualUrl != null && !manualUrl.isBlank()) ? manualUrl : null;
        if (coverImage != null && !coverImage.isEmpty()) {
            // In production: save to storage and get URL. Here we use the filename as placeholder.
            imageUrl = "/images/books/" + coverImage.getOriginalFilename();
        }

        bookService.addBook(form, imageUrl);
        ra.addFlashAttribute("flashSuccess", "\"" + form.getTitle() + "\" added to catalog.");
        return "redirect:/admin/dashboard";
    }

    /* ── Add Book dedicated page ──────────────────────────────────────── */

    @GetMapping("/books/new")
    public String addBookPage(Model model) {
        model.addAttribute("bookForm", new BookCreateRequest());
        return "add-book";
    }

    /* ── Delete Book ──────────────────────────────────────────────────── */

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) {
        bookService.deleteBook(id);
        ra.addFlashAttribute("flashSuccess", "Book removed from catalog.");
        return "redirect:/admin/inventory";
    }

    /* ── Inventory ────────────────────────────────────────────────────── */

    @GetMapping("/inventory")
    public String inventory(Model model) {
        List<Book> books = bookService.getAllBooks();
        int lowStock = 0;
        for (Book b : books) {
            if (b.getStockQuantity() > 0 && b.getStockQuantity() < 5) {
                lowStock++;
            }
        }
        int outOfStock = 0;
        for (Book b : books) {
            if (b.getStockQuantity() == 0) {
                outOfStock++;
            }
        }

        model.addAttribute("books",          books);
        model.addAttribute("totalBooks",     books.size());
        model.addAttribute("lowStockCount",  lowStock);
        model.addAttribute("outOfStockCount",outOfStock);
        return "admin-inventory";
    }

    /* ── Admin Orders ─────────────────────────────────────────────────── */

    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> all = orderService.getAllOrders();
        int pendingCount = 0;
        for (Order o : all) {
            if (o.getStatus() == Order.Status.PENDING || o.getStatus() == Order.Status.PROCESSING) {
                pendingCount++;
            }
        }
        int shippedCount = 0;
        for (Order o : all) {
            if (o.getStatus() == Order.Status.SHIPPED || o.getStatus() == Order.Status.DELIVERED) {
                shippedCount++;
            }
        }
        model.addAttribute("allOrders",    all);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("shippedCount", shippedCount);
        model.addAttribute("totalRevenue", orderService.getTotalSales());
        return "admin-orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes ra) {
        orderService.updateStatus(id, Order.Status.valueOf(status));
        ra.addFlashAttribute("flashSuccess", "Order status updated.");
        return "redirect:/admin/orders";
    }

    /* ── Settings ─────────────────────────────────────────────────────── */

    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin-settings";
    }

    @PostMapping("/settings/store")
    public String saveSettings(RedirectAttributes ra) {
        ra.addFlashAttribute("flashSuccess", "Settings saved.");
        return "redirect:/admin/settings";
    }
}
