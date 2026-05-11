package com.example.Ap.service;

import com.example.Ap.dto.BookCreateRequest;
import com.example.Ap.model.Book;
import com.example.Ap.repository.BookRepository;
import com.example.Ap.repository.CartItemRepository;
import com.example.Ap.repository.OrderItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository      bookRepository;
    private final CartItemRepository  cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager       entityManager;

    public Optional<Book> getFeaturedBook() {
        return bookRepository.findFirstByFeaturedTrue();
    }

    public List<Book> getNewArrivals() {
        return bookRepository.findTop8ByOrderByAddedDateDesc();
    }
    public List<Book> getRecentBooks() {
        return bookRepository.findTop8ByOrderByAddedDateDesc();
    }


    public List<Book> getRareFinds() {
        return bookRepository.findByBadge("Rare Find");
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll(Sort.by("addedDate").descending());
    }

    public long countAll() {
        return bookRepository.count();
    }

    public List<String> getAllGenres() {
        return bookRepository.findAllGenres();
    }

    public List<Book> getCatalog(List<String> genres, String minPriceStr, String maxPriceStr,
                                  String q, String sort) {
        BigDecimal minPrice  = parse(minPriceStr);
        BigDecimal maxPrice  = parse(maxPriceStr);
        List<String> genreFilter = (genres == null || genres.isEmpty()) ? null : genres;
        String search        = (q == null || q.isBlank()) ? null : q.trim();

        List<Book> results = bookRepository.findWithFilters(genreFilter, minPrice, maxPrice, search);

        // Sort the results in Java after fetching
        Comparator<Book> comparator = switch (sort == null ? "popular" : sort) {
            case "price_asc"  -> Comparator.comparing(Book::getPrice);
            case "price_desc" -> Comparator.comparing(Book::getPrice).reversed();
            case "newest"     -> Comparator.comparing(Book::getAddedDate).reversed();
            default           -> Comparator.comparing(Book::getId).reversed();
        };

        results.sort(comparator);
        return results;
    }

    @Transactional
    public void addBook(BookCreateRequest req, String coverImageUrl) {
            bookRepository.save(Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn())
                .genre(req.getGenre())
                .description(req.getDescription())
                .price(req.getPrice())
                .stockQuantity(req.getStockQuantity())
                .badge(req.getBadge())
                .featured(req.isFeatured())
                .coverImageUrl(coverImageUrl)
                .build());
    }

    /**
     * safe book deletion:
     *  1. Native SQL deletes from cart_items  — bypasses Hibernate entity cache
     *  2. Native SQL deletes from order_items — bypasses Hibernate entity cache
     *  3. flush() + clear() evicts the first-level cache so no stale references remain
     *  4. Delete the book
     */
    @Transactional
    public void deleteBook(Long id) {
        cartItemRepository.deleteByBookId(id);
        orderItemRepository.deleteByBookId(id);
        entityManager.flush();
        entityManager.clear();
        bookRepository.deleteById(id);
    }

    private BigDecimal parse(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s); } catch (NumberFormatException e) { return null; }
    }
}
