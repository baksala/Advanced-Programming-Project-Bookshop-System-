package com.example.Ap.controller;

import com.example.Ap.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping("/")
    public String home(Model model) {
        bookService.getFeaturedBook().ifPresent(b -> model.addAttribute("featuredBook", b));
        model.addAttribute("newArrivals", bookService.getNewArrivals());
        return "UserHome";
    }

    @GetMapping("/new-arrivals")
    public String newArrivals(Model model) {
        model.addAttribute("books", bookService.getNewArrivals());
        return "new-arrivals";
    }

    @GetMapping("/rare-finds")
    public String rareFinds(Model model) {
        model.addAttribute("books", bookService.getRareFinds());
        return "rare-finds";
    }

    @GetMapping("/about")    public String about()    { return "about"; }
    @GetMapping("/terms")    public String terms()    { return "terms"; }
    @GetMapping("/privacy")  public String privacy()  { return "privacy"; }
    @GetMapping("/shipping") public String shipping() { return "shipping"; }
    @GetMapping("/contact")  public String contact()  { return "contact"; }
}
