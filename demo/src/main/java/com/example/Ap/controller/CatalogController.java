package com.example.Ap.controller;

import com.example.Ap.model.Book;
import com.example.Ap.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final BookService bookService;

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) List<String> genre,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String q,
            Model model) {

        List<Book> books = bookService.getCatalog(genre, minPrice, maxPrice, q, sort);

        model.addAttribute("books",           books);
        model.addAttribute("totalElements",   books.size());
        model.addAttribute("availableGenres", bookService.getAllGenres());
        model.addAttribute("selectedGenres",  genre != null ? genre : List.of());
        model.addAttribute("minPrice",        minPrice);
        model.addAttribute("maxPrice",        maxPrice);
        model.addAttribute("sort",            sort);
        model.addAttribute("q",               q);
        return "BExplore";
    }
}
