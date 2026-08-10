package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
 * Spring configuration class dedicated to the test environment setup and database seeding.
 *
 * Responsibilities:
 * - Configures settings specific to the "test" Spring profile (@Profile("test")).
 * - Executes database seeding via {@link CommandLineRunner} at application startup.
 * - Isolates test data preparation logic from production environments.
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
    // Injects the UserRepository dependency cleanly without reflection.
    // Enables 'final' field immutability and safe testing.
    // =========================================================
    private final UserRepository userRepository;

    public TestDataConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ========================================================================
    // STARTUP EXECUTION (CommandLineRunner)
    // ========================================================================
    @Override
    public void run(String... args) throws Exception {
        /*
         * Database Seeding:
         * All code inside run() executes automatically upon startup
         * when the "test" profile is active.
         */
        User u1 = new User(null, "Domingos Dinis", "Domingos@yahoo.com", "61984615325", "1985");
        User u2 = new User(null, "Maria Silva", "Maria@yahoo.com", "61984615326", "1990");
        User u3 = new User(null, "Carlos Santos", "Carlos@yahoo.com", "61984615327", "1988");
        User u4 = new User(null, "Ana Oliveira", "Ana@yahoo.com", "61984615328", "1992");

        // Saving mock records into the database
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));
    }
}

/*
 ============================================================================
 APPLICATION STARTUP & PROFILES EXECUTION FLOW
 ============================================================================

 Application Startup (mvn spring-boot:run)
       │
       ▼
 Read application.properties / application-test.properties
       │
       ▼
 Active Profile = "test"?
       ├──► NO  ──► Skip TestDataConfig Bean creation.
       │
       └──► YES ──► Instantiate TestDataConfig
                         │
                         ▼
                   Inject UserRepository via Constructor
                         │
                         ▼
                   Trigger CommandLineRunner.run()
                         │
                         ▼
                   Instantiate User objects (u1, u2, u3, u4)
                         │
                         ▼
                   userRepository.saveAll(...) ──► H2 Database Populated
 ============================================================================
*/