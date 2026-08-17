package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.entities.Order;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.diniz.springbootstudy.repositories.CategoryRepository;
import com.diniz.springbootstudy.repositories.OrderRepository;
import com.diniz.springbootstudy.repositories.OrderRepository01;
import com.diniz.springbootstudy.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

    public TestDataConfig(
            UserRepository userRepository,
            OrderRepository orderRepository,
            OrderRepository01 orderRepository01,
            CategoryRepository categoryRepository
    ) {

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderRepository01 = orderRepository01;
        this.categoryRepository = categoryRepository;
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

        Category cat1 = new Category(null,"Library");
        Category cat2 = new Category(null,"Electronic");
        Category cat3 = new Category(null,"Computer");

        // Saving mock users into the database
        categoryRepository.saveAll(Arrays.asList(cat1,cat2,cat3));

        // ====================================================================
        // CREATE USERS
        // ====================================================================

        User u1 = new User(
                null,
                "Domingos Dinis",
                "Domingos@yahoo.com",
                "61984615325",
                "1985"
        );

        User u2 = new User(
                null,
                "Maria Silva",
                "Maria@yahoo.com",
                "61984615326",
                "1990"
        );

        User u3 = new User(
                null,
                "Carlos Santos",
                "Carlos@yahoo.com",
                "61984615327",
                "1988"
        );

        User u4 = new User(
                null,
                "Ana Oliveira",
                "Ana@yahoo.com",
                "61984615328",
                "1992"
        );

        User u5 = new User(
                null,
                "João Pereira",
                "Joao@yahoo.com",
                "61984615329",
                "1987"
        );

        User u6 = new User(
                null,
                "Fernanda Costa",
                "Fernanda@yahoo.com",
                "61984615330",
                "1995"
        );

        User u7 = new User(
                null,
                "Ricardo Almeida",
                "Ricardo@yahoo.com",
                "61984615331",
                "1983"
        );

        User u8 = new User(
                null,
                "Juliana Martins",
                "Juliana@yahoo.com",
                "61984615332",
                "1991"
        );

        // Saving mock users into the database
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4,u5,u6,u7,u8));

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

        Order o1 = new Order(
                null,
                Instant.parse("2026-08-13T10:30:00Z"),
                OrderStatus.PAID,
                u1 // this order is associated to the client u1
        );

        Order o2 = new Order(
                null,
                Instant.parse("2026-08-13T11:45:00Z"),
                OrderStatus.WAITING_PAYMENT,
                u2
        );

        Order o3 = new Order(
                null,
                Instant.parse("2026-08-13T14:20:00Z"),
                OrderStatus.PAID,
                u3
        );

        Order o4 = new Order(
                null,
                Instant.parse("2026-08-13T16:00:00Z"),
                OrderStatus.DELIVERED,
                u4
        );

        // Saving mock orders into the database
        orderRepository.saveAll(Arrays.asList(o1, o2, o3, o4));

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
       Inject UserRepository
       Inject OrderRepository
              │
              ▼
       Trigger CommandLineRunner.run()
              │
              ▼
       Create User objects
       (u1, u2, u3, u4)
              │
              ▼
       userRepository.saveAll(...)
              │
              ▼
       H2 Database
       Users populated
              │
              ▼
       Create Order objects
       (o1, o2, o3, o4)
              │
              ▼
       orderRepository.saveAll(...)
              │
              ▼
       H2 Database
       Orders populated

============================================================================
*/