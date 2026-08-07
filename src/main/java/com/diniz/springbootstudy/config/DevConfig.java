package com.diniz.springbootstudy.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/* ============================================================================================
 * MOTIVATION AND PURPOSE OF THIS CLASS (DevConfig / H2ConsoleConfig):
 * ============================================================================================
 *
 * 1. SPRING BOOT 3+ AND JAKARTA EE SUPPORT:
 *    Starting with Spring Boot 3.x, the Java ecosystem migrated from the legacy 'javax.servlet'
 *    package to the new 'jakarta.servlet' package (compatible with Tomcat 10+). The legacy H2
 *    console class (org.h2.server.web.WebServlet) relied on 'javax' and causes runtime errors
 *    in recent versions. This configuration explicitly registers 'JakartaWebServlet', fixing that issue.
 *
 * 2. ENVIRONMENT ISOLATION & SECURITY:
 *    The H2 Console is an administrative web interface that allows raw SQL execution against the DB.
 *    By binding this configuration strictly to the 'dev' and 'test' profiles, we ensure the
 *    '/h2-console' endpoint is only accessible during local development and testing, keeping it
 *    disabled in Production environments.
 *
 * ============================================================================================
 */

/*
 * STEP 1: Declare this class as a Spring configuration class.
 * Spring scans the project on startup and reads this class to register managed Beans.
 */
@Configuration

/*
 * STEP 2: Restrict execution of this configuration to "dev" and "test" profiles.
 * Allows the H2 web console to run during local development as well as during automated
 * test suite execution with the in-memory H2 database.
 */
@Profile({"dev", "test"})
public class DevConfig {

    /*
     * STEP 3: Define the Servlet Bean factory method.
     * The @Bean annotation indicates that the return value of this method will be managed by the Spring container.
     * We return a 'ServletRegistrationBean', which is Spring Boot's way of registering custom Servlets
     * directly into the embedded web server (e.g., Tomcat).
     */
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2servletRegistration() {

        /*
         * STEP 4: Instantiate the Jakarta EE compatible Servlet.
         */
        ServletRegistrationBean<JakartaWebServlet> registration =
                new ServletRegistrationBean<>(new JakartaWebServlet());

        /*
         * STEP 5: Map both "/h2-console" and "/h2-console/*" URL routes.
         * Mapping both variants prevents 404 errors when accessing the console without a trailing slash.
         */
        registration.addUrlMappings("/h2-console", "/h2-console/*");

        /*
         * STEP 6: Assign a registration name for the Servlet.
         * Gives the Servlet an internal identifier ("H2Console") within the Spring application context.
         */
        registration.setName("H2Console");

        /*
         * STEP 7: Return the fully configured Bean to be registered into the Web container.
         */
        return registration;
    }
}

// Access URL: http://localhost:8080/h2-console
// Useful commands:
// ./mvnw clean spring-boot:run (recompiles project from scratch, cleaning the target directory)
// ./mvnw spring-boot:run (standard daily execution command)

/*
 Spring Boot Startup Execution Flow:

        Spring Boot Starts
                │
                ▼
      Finds @Configuration
                │
                ▼
         Finds DevConfig
                │
                ▼
 Is "dev" or "test" profile active?
                │
               Yes
                │
                ▼
     Executes the @Bean method
                │
                ▼
     new JakartaWebServlet()
                │
                ▼
    ServletRegistrationBean
                │
                ▼
            Maps routes:
     /h2-console and /h2-console/*
                │
                ▼
 Tomcat registers the Servlet
                │
                ▼
  http://localhost:8080/h2-console
 */