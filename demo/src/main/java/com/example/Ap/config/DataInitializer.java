package com.example.Ap.config;

import com.example.Ap.model.Book;
import com.example.Ap.model.User;
import com.example.Ap.repository.BookRepository;
import com.example.Ap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ── Admin user ─────────────────────────────────────────────────────
        if (!userRepository.existsByEmail("admin@bibliotech.com")) {
            userRepository.save(User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@bibliotech.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(User.Role.ADMIN)
                    .memberSince(LocalDate.now())
                    .build());
            System.out.println(">>> Seeded admin: admin@bibliotech.com / admin1234");
        }

        // ── Demo reader ────────────────────────────────────────────────────
        if (!userRepository.existsByEmail("reader@bibliotech.com")) {
            userRepository.save(User.builder()
                    .firstName("Eleanor")
                    .lastName("Vance")
                    .email("reader@bibliotech.com")
                    .password(passwordEncoder.encode("reader1234"))
                    .bio("Primarily interested in 19th-century gothic fiction.")
                    .role(User.Role.USER)
                    .memberSince(LocalDate.of(2024, 1, 1))
                    .build());
            System.out.println(">>> Seeded reader: reader@bibliotech.com / reader1234");
        }

        // ── Sample books ──────────────────────────────────────────────────
        if (bookRepository.count() == 0) {
            bookRepository.save(Book.builder()
                    .title("The Solitude of Prime Numbers")
                    .author("Paolo Giordano")
                    .genre("Fiction")
                    .price(new BigDecimal("24.00"))
                    .stockQuantity(8)
                    .badge("Bestseller")
                    .featured(true)
                    .description("A stunning debut about two lost souls and their parallel lives.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCywOp43lATCSXa0YTrNrVyp2L044cO4KFbTbL6eAcPBRcMTRJ0b-Ti2-E7RTizMhGVTqMsZtwo3vQYMWipor2fy9rO3I3SISaygWqqDXJ4oDjUPQfAWxlUMuLUQMS2A10QUbKu8t_F4v9rxE_ZB9wFNL2l2rGDLVSwCL6aMf-9pDBITyocUChQwy_OMHwqoPkLbQpWwsSzLLypwYp7onZl9xzz1uEnoqUqUkozUxsLqAa_TQqK_E9WEoziv8xZC4AQEXHwzAr0Akk")
                    .addedDate(LocalDate.now().minusDays(1))
                    .build());

            bookRepository.save(Book.builder()
                    .title("Meditations")
                    .author("Marcus Aurelius")
                    .genre("Philosophy")
                    .price(new BigDecimal("18.50"))
                    .stockQuantity(15)
                    .description("The personal journal of the Roman Emperor and Stoic philosopher.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCx6v9LCO48QofpYHAf-cwiF7TgSc47oCiHw412se-xooujSdyNOsnt5jOOnmkqzTV9o873m9vSIURcycEpZtq4oHEA-01gxBQJPAVJFor6rA7eMz8YQLhDBFhh35WJyqZyqxakSlJ4W28N7ePNTnmIOFNvz1OChT3_zFVoJ85fMsIhjQ5cv_oKixm_3QBn6Y_wqmxXXCX6HFkmgPE8U7kthHvPXEcrfCQJDrF6RCi9fs2U4Xz2fntWps2VnrKlWxivbhe8QNkjcv0")
                    .addedDate(LocalDate.now().minusDays(3))
                    .build());

            bookRepository.save(Book.builder()
                    .title("Leaves of Grass")
                    .author("Walt Whitman")
                    .genre("Poetry")
                    .price(new BigDecimal("45.00"))
                    .stockQuantity(3)
                    .badge("Rare Find")
                    .description("The landmark collection of American poetry, first published in 1855.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCRYLzI-kSwNf-1YGIE4Gq725r0cDPU57c0QfDS2ljfgmlmWFH2Ur0FYONKGjHHonYcsvwb39J5NmQT-dY69KmqSFliOuZU0y8y-FslBumI8TyDVOy2rRK6WeCINDZyohNrdfF-AMx-PIuI3_oTqUbpjq9JC3HVyERr7JsCu3HrW-8ro-yvtf7DrIRnmqZI-4iOSa2qJBcnfFcyCSLZos6-mgClWyBa0u5TQ5fyX8NHH9eOL67O5gw3nBsbkyfVFRvm9VMwthVsRY8")
                    .addedDate(LocalDate.now().minusDays(5))
                    .build());

            bookRepository.save(Book.builder()
                    .title("The Design of Everyday Things")
                    .author("Don Norman")
                    .genre("Non-Fiction")
                    .price(new BigDecimal("22.95"))
                    .stockQuantity(10)
                    .description("The bible of user-centered design, revised and expanded.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuBRSx3DsChPXp1ooSul7vx7_E_NO5-UkyptBKkHklFGwM_fSU_Vt7nPBFlpIKpFsUG7Mw3fjric8Y3ej0fM9SrjmE1nOd4_GvbPSpaJ_7fqKSBS2laQVDsplQCHCgHSFa__yLAy4mEXOL9_pGyaN0Ogv6hMNr-0LZXdUi3S-itBU4gLjo12F3ICJ6J_VkUOgK02h1qgoleLNI-Zk0D0ud2uRszN_WUPrZTu-BNCnjhWPNeu-aRcJ0vDefkwbQ-KWlgdKah406kq-TI")
                    .addedDate(LocalDate.now().minusDays(7))
                    .build());

            bookRepository.save(Book.builder()
                    .title("To the Lighthouse")
                    .author("Virginia Woolf")
                    .genre("Fiction")
                    .price(new BigDecimal("16.00"))
                    .stockQuantity(12)
                    .description("A masterpiece of modernist fiction exploring consciousness, time, and loss.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCqTkDMuBcG8aQ4BZj-cB5rs7LNPYo5SspJYn7xoGLlTE-lP2gb89WeTbg7ChIFkiJeVkm9edLp1tUdxFsW9db9EnsInkqDLUQVQ38digKiZxPPnxPfFBV6ZpWJFXfEJZZlngdJdznfbLvBDDDGp_GzuuK-t8KhVylAEXswIqvNTVMO0DmE8giht3YQQjBtGrb34pJ-8N-zWRyddGBbKXcvRq3KjjDdAGO1Q-rb72rPsrNE5HbTP7qJIzQwwJ7RlJijvQXRqi5V3rg")
                    .addedDate(LocalDate.now().minusDays(9))
                    .build());

            bookRepository.save(Book.builder()
                    .title("A Brief History of Time")
                    .author("Stephen Hawking")
                    .genre("Non-Fiction")
                    .price(new BigDecimal("19.50"))
                    .stockQuantity(20)
                    .description("An accessible introduction to the concepts of modern physics and cosmology.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuBE5muVU3yNZVbhGVQCkSZjxdgeNsCM14z3PiHh39TeVThXci-lUqEFuMmGWEDgvUX_X6P86EaN3ComfnhqvF07kCC07w1GMoY5uxKBomLbZxve-W9-oA1Mc0voJm3z4K6XUk74PFrVKyb6KTeHGl5RAaxdk6lZx8iuJeCP0mebO4CXqfvWK3NT3zPCLIN8M1bvgR8wHnXtm3N2y4hzgbHLGj4PciSrtlxf6ExL_d_FCl3IOGd6UPnoPfdpvI8Uy0XGim6cq_lX3FU")
                    .addedDate(LocalDate.now().minusDays(11))
                    .build());

            bookRepository.save(Book.builder()
                    .title("First Folio Facsimile")
                    .author("William Shakespeare")
                    .genre("Drama")
                    .price(new BigDecimal("285.00"))
                    .stockQuantity(2)
                    .badge("Rare Find")
                    .description("A museum-quality reproduction of Shakespeare's First Folio, 1623.")
                    .coverImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCy59dg4OtevGp6pNr9esdmhf0RDck-8RDlPbIAcrZ5eYyamdsJMZdSvicTcGtY2ziAA9cZxL1bd1dVdFNJHrSHDHFIihpkfxcSM1uEZL-63y7jP8dnFImaESBG17t-h3u_ow7nqQJs-gPefYLtGoMN5oljggE3qGn3fizSVpiFLjArtDHpf6WbQLeN7L_7tEpzl-Otqh1sfL3qT8wXmwr-wvU3N61I1jcqP5BDEVzU4NsavFlD8OGghFmH7rgnouCdhhfWc3GHVjc")
                    .addedDate(LocalDate.now().minusDays(14))
                    .build());

            System.out.println(">>> Seeded 7 sample books");
        }
    }
}
