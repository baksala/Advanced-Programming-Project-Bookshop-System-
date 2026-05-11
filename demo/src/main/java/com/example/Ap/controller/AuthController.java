package com.example.Ap.controller;

import com.example.Ap.dto.RegisterRequest;
import com.example.Ap.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ── Login ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @CookieValue(value = "rememberedEmail", defaultValue = "") String rememberedEmail,
            Model model) {

        if (error  != null) model.addAttribute("loginError",    "Invalid email or password. Please try again.");
        if (logout != null) model.addAttribute("logoutMessage", "You have been signed out successfully.");

        if (!rememberedEmail.isEmpty()) {
            model.addAttribute("rememberedEmail", rememberedEmail);
            model.addAttribute("rememberMe", true);
        }

        return "Login";
    }

    /**
     * bonus: Cookie Handling
     * The form posts here when "Remember Me" is checked.
     * Sets the rememberedEmail cookie, then forwards the credentials
     * to Spring Security's own /login processor.
     *
    */
    @PostMapping("/login-remember")
    public String loginRemember(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String rememberMe,
            HttpServletResponse response) {

        if (rememberMe != null) {
            Cookie emailCookie = new Cookie("rememberedEmail", username);
            emailCookie.setMaxAge(7 * 24 * 60 * 60);
            emailCookie.setPath("/");
            emailCookie.setHttpOnly(true);
            response.addCookie(emailCookie);
        } else {
            Cookie clear = new Cookie("rememberedEmail", "");
            clear.setMaxAge(0);
            clear.setPath("/");
            response.addCookie(clear);
        }


        return "forward:/login";
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "Register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest req,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        if (req.getPassword() != null && req.getConfirmPassword() != null
                && req.isPasswordsMissmMatch()) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }

        if (result.hasErrors()) {
            return "Register";
        }

        try {
            userService.register(req);
            ra.addFlashAttribute("successMessage", "Account created! Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "Register";
        }
    }
}
