package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.entities.*;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.diniz.springbootstudy.entities.enums.UserRole;
import com.diniz.springbootstudy.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;

// ============================================================================
// CONFIGURATION LAYER & DATABASE SEEDING
// ============================================================================
// Purpose:
// Executed at application startup to populate the H2 in-memory database
// with mock data exclusively for testing/development environments.
//
// Lifecycle:
// 1. Spring Context starts up with active profile "test".
// 2. TestDataConfig Bean is created via Constructor Injection.
// 3. CommandLineRunner.run() is executed automatically after context initialization.
// ============================================================================

/**
 * Spring configuration class dedicated to the test environment setup
 * and database seeding.
 * <p>
 * Responsibilities:
 * - Configures settings specific to the "test" Spring profile.
 * - Executes database seeding via {@link CommandLineRunner} at application startup.
 * - Isolates test data preparation logic from production environments.
 */
@Configuration
@Profile("test")
public class TestDataConfig implements CommandLineRunner {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    private final UserRepository userRepository;
    private final OrderRepository01 orderRepository01;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataConfig(
            UserRepository userRepository,
            OrderRepository01 orderRepository01,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.orderRepository01 = orderRepository01;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========================================================================
    // STARTUP EXECUTION (CommandLineRunner)
    // ========================================================================

    @Override
    public void run(String... args) throws Exception {

        /*
         * Database Seeding:
         * All code inside run() executes automatically upon application startup
         * when the "test" profile is active.
         */

        // ====================================================================
        // CATEGORY IS AN INDEPENDENT CLASS
        // ====================================================================

        Category cat1 = new Category(null, "Library");
        Category cat2 = new Category(null, "Electronic");
        Category cat3 = new Category(null, "Computer");

        // Saving mock categories into the database
        categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));

        // ====================================================================
        // CREATE PRODUCTS & ASSOCIATE WITH CATEGORIES
        // ====================================================================

        Product p1 = new Product(
                null,
                "The Lord of the Rings",
                "An epic fantasy novel written by J.R.R. Tolkien.",
                90.5,
                ""
        );

        Product p2 = new Product(
                null,
                "Samsung Smart TV 55 Inch",
                "Smart television with 4K resolution and advanced features.",
                2190.0,
                ""
        );

        Product p3 = new Product(
                null,
                "Apple MacBook Pro",
                "Professional laptop with powerful performance and premium design.",
                1250.0,
                ""
        );

        Product p4 = new Product(
                null,
                "Gaming Desktop PC",
                "High-performance gaming computer with advanced hardware.",
                1200.0,
                ""
        );

        Product p5 = new Product(
                null,
                "Rails for Beginners",
                "A beginner-friendly book for learning Ruby on Rails.",
                100.99,
                ""
        );

        // Initial save for products
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // Associating products with categories (Many-to-Many relationship)
        p1.getCategories().add(cat1);
        p2.getCategories().add(cat2);
        p2.getCategories().add(cat3);
        p3.getCategories().add(cat3);
        p4.getCategories().add(cat3);
        p5.getCategories().add(cat1);

        // Saving updated associations to the database
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // ====================================================================
        // CREATE USERS (WITH ROLES & BCRYPT PASSWORDS)
        // ====================================================================

        // User 1: ADMIN (Full access)
        User u1 = new User(
                null,
                "Domingos Dinis",
                "Domingos@yahoo.com",
                "61984615325",
                passwordEncoder.encode("Matrix21!!"),
                UserRole.ADMIN
        );

        /*
        {
          "email": "Domingos@yahoo.com",
          "password": "Matrix21!!"
        }
         */

        // User 2: CLIENT
        User u2 = new User(
                null,
                "Maria Silva",
                "Maria@yahoo.com",
                "61984615326",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Maria@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 3: CLIENT
        User u3 = new User(
                null,
                "Carlos Santos",
                "Carlos@yahoo.com",
                "61984615327",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Carlos@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 4: CLIENT
        User u4 = new User(
                null,
                "Ana Oliveira",
                "Ana@yahoo.com",
                "61984615328",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Ana@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 5: CLIENT
        User u5 = new User(
                null,
                "João Pereira",
                "Joao@yahoo.com",
                "61984615329",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Joao@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 6: CLIENT
        User u6 = new User(
                null,
                "Fernanda Costa",
                "Fernanda@yahoo.com",
                "61984615330",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Fernanda@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 7: CLIENT
        User u7 = new User(
                null,
                "Ricardo Almeida",
                "Ricardo@yahoo.com",
                "61984615331",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Ricardo@yahoo.com",
          "password": "Senha123!"
        }
         */

        // User 8: CLIENT
        User u8 = new User(
                null,
                "Juliana Martins",
                "Juliana@yahoo.com",
                "61984615332",
                passwordEncoder.encode("Senha123!"),
                UserRole.CLIENT
        );

        /*
        {
          "email": "Juliana@yahoo.com",
          "password": "Senha123!"
        }
         */

        // Saving mock users into the database
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5, u6, u7, u8));

        // ====================================================================
        // CREATE ORDERS
        // ====================================================================

        Order01 o1 = new Order01(
                null,
                Instant.parse("2026-08-13T10:30:00Z"),
                OrderStatus.PAID,
                u1
        );

        Order01 o2 = new Order01(
                null,
                Instant.parse("2026-08-13T11:45:00Z"),
                OrderStatus.WAITING_PAYMENT,
                u2
        );

        Order01 o3 = new Order01(
                null,
                Instant.parse("2026-08-13T14:20:00Z"),
                OrderStatus.PAID,
                u3
        );

        Order01 o4 = new Order01(
                null,
                Instant.parse("2026-08-13T16:00:00Z"),
                OrderStatus.DELIVERED,
                u4
        );

        // ====================================================================
        // CREATE ORDERS01
        // ====================================================================
        Order01 ord1 = new Order01(
                null,
                Instant.parse("2026-08-13T10:30:00Z"),
                OrderStatus.WAITING_PAYMENT,
                u5
        );

        Order01 ord2 = new Order01(
                null,
                Instant.parse("2026-08-14T14:45:00Z"),
                OrderStatus.PAID,
                u6
        );

        Order01 ord3 = new Order01(
                null,
                Instant.parse("2026-08-15T09:15:00Z"),
                OrderStatus.SHIPPED,
                u7
        );

        Order01 ord4 = new Order01(
                null,
                Instant.parse("2026-08-16T18:20:00Z"),
                OrderStatus.DELIVERED,
                u8
        );

        // Saving all mock orders into the database
        orderRepository01.saveAll(Arrays.asList(o1, o2, o3, o4, ord1, ord2, ord3, ord4));

        // ====================================================================
        // CREATE ORDER ITEMS
        // ====================================================================

        OrderItem oi1 = new OrderItem(o1, p1, p1.getPrice(), 2);
        OrderItem oi2 = new OrderItem(o1, p3, p3.getPrice(), 1);
        OrderItem oi3 = new OrderItem(o2, p3, p3.getPrice(), 2);
        OrderItem oi4 = new OrderItem(o3, p5, p5.getPrice(), 2);
        OrderItem oi5 = new OrderItem(o4, p2, p2.getPrice(), 1);

        OrderItem oi6 = new OrderItem(ord1, p1, p1.getPrice(), 2);
        OrderItem oi7 = new OrderItem(ord1, p3, p3.getPrice(), 1);
        OrderItem oi8 = new OrderItem(ord2, p3, p3.getPrice(), 2);
        OrderItem oi9 = new OrderItem(ord3, p5, p5.getPrice(), 2);
        OrderItem oi10 = new OrderItem(ord4, p2, p2.getPrice(), 1);

        // Saving mock order items into the database
        orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4, oi5, oi6, oi7, oi8, oi9, oi10));

        // ====================================================================
        // CREATE PAYMENT (1:1 RELATIONSHIP WITH ORDER)
        // ====================================================================

        Payment pay1 = new Payment(null, Instant.parse("2026-08-13T21:00:00Z"), ord1);
        ord1.setPayment(pay1);

        // Saving the parent Order entity (CascadeType.ALL persists Payment)
        orderRepository01.save(ord1);

        // ====================================================================
        // DATABASE SEEDING COMPLETE
        // ====================================================================
    }
}


/*
const jsonData = pm.response.json();

console.log("Resposta completa:");
console.log(jsonData);

pm.environment.set("token", jsonData.LoginResponseDTO.token);

console.log("Token salvo:");
console.log(jsonData.LoginResponseDTO.token);
 */

/*
{
  "name": "Joao Oliveira",
  "email": "joao.oliveira@yahoo.com",
  "phone": "61987654321",
  "password": "Senha456!"
}
 */