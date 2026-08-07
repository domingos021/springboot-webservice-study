package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;


/**
 * Spring configuration class dedicated to the test environment setup and database seeding,
 * POPULATING the H2 database with mock data for testing purposes.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Configures beans and settings specific to the <b>"test"</b> Spring profile.</li>
 *   <li>Executes database seeding via {@link CommandLineRunner} at application startup to populate the H2 database with mock test data.</li>
 *   <li>Isolates test data preparation logic from development and production environments.</li>
 * </ul>
 */
@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    /*
     * Dependency Injection means that an object receives
     * the dependencies it needs from an external source,
     * instead of creating those dependencies itself.
     */
    @Autowired // Injects the UserRepository dependency into this field automatically, do not need to instantiate it manually with constructor.
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        //SEED
        // all codes in here, are executed automatically when the application starts, but only if the "test" profile is active.
        // Database seeding logic goes here (e.g., userRepository.saveAll(...))

        User u1 = new User(null, "Domingos Dinis", "Domingos@yahoo.com", "61984615325", "1985");
        User u2 = new User(null, "Maria Silva", "Maria@yahoo.com", "61984615326", "1990");
        User u3 = new User(null, "Carlos Santos", "Carlos@yahoo.com", "61984615327", "1988");
        User u4 = new User(null, "Ana Oliveira", "Ana@yahoo.com", "61984615328", "1992");

        //saving on database
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));
    }
}