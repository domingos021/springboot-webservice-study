package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.entities.Order;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.OrderItem;
import com.diniz.springbootstudy.entities.Product;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.diniz.springbootstudy.repositories.CategoryRepository;
import com.diniz.springbootstudy.repositories.OrderItemRepository;
import com.diniz.springbootstudy.repositories.OrderRepository;
import com.diniz.springbootstudy.repositories.OrderRepository01;
import com.diniz.springbootstudy.repositories.ProductRepository;
import com.diniz.springbootstudy.repositories.UserRepository;
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
 * // * - Executes database seeding via {@link CommandLineRunner} at application startup.
 * // * - Isolates test data preparation logic from production environments.
 */
@Configuration
@Profile("test")
public class TestDataConfig implements CommandLineRunner {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     // @Autowired
     // private UserRepository userRepository;
    */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // Injects dependencies through the constructor.
    // Enables 'final' fields and makes the class easier to test.
    // =========================================================
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderRepository01 orderRepository01;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataConfig(
            UserRepository userRepository,
            OrderRepository orderRepository,
            OrderRepository01 orderRepository01,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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
        /*
         * We access the Product object (p1).
         * The getCategories() method returns the Set<Category> associated with the product.
         * Then, we add the category cat1 to that collection, creating the relationship
         * between product p1 and category cat1.
         */
        p1.getCategories().add(cat1); // association between objects (products and categories)
        p2.getCategories().add(cat2);
        p2.getCategories().add(cat3);
        p3.getCategories().add(cat3);
        p4.getCategories().add(cat3);
        p5.getCategories().add(cat1);

        // Saving updated associations to the database
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // ====================================================================
        // CREATE USERS
        // ====================================================================

        User u1 = new User(
                null,
                "Domingos Dinis",
                "Domingos@yahoo.com",
                "61984615325",
                passwordEncoder.encode("Senha123!")
        );

        User u2 = new User(
                null,
                "Maria Silva",
                "Maria@yahoo.com",
                "61984615326",
                passwordEncoder.encode("Senha123!")
        );

        User u3 = new User(
                null,
                "Carlos Santos",
                "Carlos@yahoo.com",
                "61984615327",
                passwordEncoder.encode("Senha123!")
        );

        User u4 = new User(
                null,
                "Ana Oliveira",
                "Ana@yahoo.com",
                "61984615328",
                passwordEncoder.encode("Senha123!")
        );

        User u5 = new User(
                null,
                "João Pereira",
                "Joao@yahoo.com",
                "61984615329",
                passwordEncoder.encode("Senha123!")
        );

        User u6 = new User(
                null,
                "Fernanda Costa",
                "Fernanda@yahoo.com",
                "61984615330",
                passwordEncoder.encode("Senha123!")
        );

        User u7 = new User(
                null,
                "Ricardo Almeida",
                "Ricardo@yahoo.com",
                "61984615331",
                passwordEncoder.encode("Senha123!")
        );

        User u8 = new User(
                null,
                "Juliana Martins",
                "Juliana@yahoo.com",
                "61984615332",
                passwordEncoder.encode("Senha123!")
        );

        // Saving mock users into the database
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5, u6, u7, u8));

        /*
         * Instant.now() captures the current date and time from the system clock.
         *
         * Order o1 = new Order(null, Instant.now(), u1);
         * Order o2 = new Order(null, Instant.now(), u2);
         * Order o3 = new Order(null, Instant.now(), u3);
         * Order o4 = new Order(null, Instant.now(), u4);
         */

        /*
         * Here, we use Instant.parse() to create specific timestamps
         * using the ISO 8601 format.
         *
         * Example:
         *
         * 2026-08-13T10:30:00Z
         * │         │        │
         * │         │        └── Z = UTC
         * │         └─────────── Time
         * └───────────────────── Date
         */

        // ====================================================================
        // CREATE ORDERS
        // ====================================================================

        Order01 o1 = new Order01(
                null,
                Instant.parse("2026-08-13T10:30:00Z"),
                OrderStatus.PAID,
                u1 // this order is associated to the client u1
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

        // Saving mock orders into the database using OrderRepository01
        orderRepository01.saveAll(Arrays.asList(o1, o2, o3, o4));

        // ====================================================================
        // CREATE ORDERS 01
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

        orderRepository01.saveAll(Arrays.asList(ord1, ord2, ord3, ord4));

        // ====================================================================
        // CREATE ORDER ITEMS
        // ====================================================================

        /*
         * OrderItem
         * ├── id (OrderItemPk)  ← Chave composta (@EmbeddedId)
         * │     ├── Order01 order    (FK → tb_order)
         * │     └── Product product  (FK → tb_product)
         * │
         * ├── Double price
         * └── Integer quantity
         *
         * Order01
         * ├── id
         * ├── moment
         * ├── status (OrderStatus)
         * └── client (User)
         *
         * Product
         * ├── id
         * ├── name
         * ├── description
         * ├── price
         * └── imgUrl
         */

        // Items associated with orders o1, o2, o3, o4
        OrderItem oi1 = new OrderItem(o1, p1, p1.getPrice(), 2);
        OrderItem oi2 = new OrderItem(o1, p3, p3.getPrice(), 1);
        OrderItem oi3 = new OrderItem(o2, p3, p3.getPrice(), 2);
        OrderItem oi4 = new OrderItem(o3, p5, p5.getPrice(), 2);
        OrderItem oi5 = new OrderItem(o4, p2, p2.getPrice(), 1);

        // Items associated with orders ord1, ord2, ord3, ord4
        OrderItem oi6 = new OrderItem(ord1, p1, p1.getPrice(), 2);
        OrderItem oi7 = new OrderItem(ord1, p3, p3.getPrice(), 1);
        OrderItem oi8 = new OrderItem(ord2, p3, p3.getPrice(), 2);
        OrderItem oi9 = new OrderItem(ord3, p5, p5.getPrice(), 2);
        OrderItem oi10 = new OrderItem(ord4, p2, p2.getPrice(), 1);

        // Saving mock order items into the database
        orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4, oi5, oi6, oi7, oi8, oi9, oi10));
    }
}


/*
============================================================================
APPLICATION STARTUP & PROFILE EXECUTION FLOW
============================================================================

Application Startup (mvn spring-boot:run)
       │
       ▼
Read application.properties
       │
       ▼
Active Profile = "test"?
       │
       ├──► NO ──► Skip TestDataConfig Bean creation
       │
       └──► YES
              │
              ▼
       Instantiate TestDataConfig
              │
              ▼
       Inject UserRepository, OrderRepository, CategoryRepository, ProductRepository, PasswordEncoder, OrderItemRepository
              │
              ▼
       Trigger CommandLineRunner.run()
              │
              ▼
       Create Category objects & Save
              │
              ▼
       Create Product objects & Save
              │
              ▼
       Associate Products <-> Categories & Save
              │
              ▼
       Create User objects & Encrypt Passwords via BCrypt & Save
              │
              ▼
       Create Order objects & Save
              │
              ▼
       Create Order01 objects & Save
              │
              ▼
       Create OrderItem objects & Save
              │
              ▼
       H2 Database fully populated

============================================================================
*/