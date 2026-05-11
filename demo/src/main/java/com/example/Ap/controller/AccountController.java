package com.example.Ap.controller;

import com.example.Ap.dto.PasswordChangeRequest;
import com.example.Ap.dto.ProfileUpdateRequest;
import com.example.Ap.model.Order;
import com.example.Ap.model.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final OrderService orderService;

    private User resolve(UserDetails p) {
        return userService.findByEmail(p.getUsername());
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = resolve(principal);
        ProfileUpdateRequest profileForm = new ProfileUpdateRequest();
        profileForm.setFirstName(user.getFirstName());
        profileForm.setLastName(user.getLastName());
        profileForm.setEmail(user.getEmail());
        profileForm.setBio(user.getBio());
        model.addAttribute("user",         user);
        model.addAttribute("profileForm",  profileForm);
        model.addAttribute("passwordForm", new PasswordChangeRequest());
        return "EditProfile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @ModelAttribute("profileForm") ProfileUpdateRequest form,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {
        User user = resolve(principal);
        if (result.hasErrors()) {
            model.addAttribute("user",         user);
            model.addAttribute("passwordForm", new PasswordChangeRequest());
            return "EditProfile";
        }
        try {
            userService.updateProfile(user, form.getFirstName(), form.getLastName(),
                    form.getEmail(), form.getBio());
            ra.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @ModelAttribute("passwordForm") PasswordChangeRequest form,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {
        User user = resolve(principal);
        if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (result.hasErrors()) {
            ProfileUpdateRequest profileForm = new ProfileUpdateRequest();
            profileForm.setFirstName(user.getFirstName());
            profileForm.setLastName(user.getLastName());
            profileForm.setEmail(user.getEmail());
            profileForm.setBio(user.getBio());
            model.addAttribute("user",         user);
            model.addAttribute("profileForm",  profileForm);
            model.addAttribute("passwordForm", form);
            return "EditProfile";
        }
        try {
            userService.changePassword(user, form.getCurrentPassword(), form.getNewPassword());
            ra.addFlashAttribute("successMessage", "Password updated successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account/profile";
    }

    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal UserDetails principal, Model model) {
        List<Order> orders = orderService.getOrdersForUser(resolve(principal));
        model.addAttribute("orders", orders);
        return "Orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            Model model) {
        User user  = resolve(principal);
        Order order = orderService.getOrderById(id);
        if (!order.getUser().getId().equals(user.getId())) return "redirect:/account/orders";
        model.addAttribute("order", order);
        return "order-detail";
    }

    @GetMapping("/wishlist")
    public String wishlist(Model model) {
        model.addAttribute("wishlistItems", Collections.emptyList());
        return "wishlist";
    }
}
