package com.diniz.springbootstudy.entities;

import com.diniz.springbootstudy.entities.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
  Java (nativo)
   │
   ├── Object
   ├── Serializable
   └── outras classes/interfaces da JDK


Spring Security (biblioteca externa)
   │
   └── UserDetails (interface)
          │
          ├── getUsername()
          ├── getPassword()
          ├── getAuthorities()
          └── outros métodos
 */

@Entity
@Table(name = "tb_user")
public class User implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    private String password;

    /*
     * Persistent UserRole enum field stored as String in the database.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role; // Defines the user type (CLIENT or ADMIN)


    @OneToMany(mappedBy = "client") // the client has an order
    private List<Order01> orders = new ArrayList<>();

    public User() {}

    public User(Long id, String name, String email, String phone, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role != null ? role : UserRole.CLIENT; // Default to CLIENT
    }

    // ========================================================================
    // SPRING SECURITY (UserDetails Contract Implementation)
    // ========================================================================


    /*
     * Returns the authorities (roles/permissions) granted to the authenticated user.
     *
     * Method name:
     * - getAuthorities() -> Returns all authorities associated with the current user.
     *
     * GrantedAuthority:
     * - A Spring Security interface that represents a permission or role granted
     *   to a user (e.g., ROLE_ADMIN, ROLE_CLIENT).
     * - Spring Security uses these authorities to determine whether a user is
     *   authorized to access protected resources (e.g., .hasRole("ADMIN")).
     *
     * Authorization flow:
     *
     * User logs in
     *      │
     *      ▼
     * Spring calls getAuthorities()
     *      │
     *      ▼
     * [ ROLE_ADMIN, ROLE_CLIENT ]
     *      │
     *      ▼
     * Spring stores these authorities
     *      │
     *      ▼
     * A protected route requires:
     *      .hasRole("ADMIN")
     *      │
     *      ▼
     * Does the user have ROLE_ADMIN?
     *      │
     *     Yes
     *      │
     *      ▼
     * Access granted
     *
     * Business rule:
     * - ADMIN users receive both ROLE_ADMIN and ROLE_CLIENT authorities,
     *   allowing access to administrator and client resources.
     * - CLIENT users receive only ROLE_CLIENT.
     */
    @Override // Implements the method required by the UserDetails interface
    @NonNull // Ensures that this method never returns null
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Check if the user has the ADMIN role
        if (this.role == UserRole.ADMIN) {

            // Return all authorities granted to an administrator
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"), // Administrator authority
                    new SimpleGrantedAuthority("ROLE_CLIENT") // Client authority inherited by admins
            );
        }

        // Return only the authority granted to a regular client
        return List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );
    }

    /*
     * Uses email as the username identifier for authentication.
     *
     * Spring Security calls this method to identify the authenticated user.
     */
    @Override
    @NonNull
    /*
     * getUsername() is a method defined by the Spring Security UserDetails interface.
     *
     * The User entity overrides this method to provide the username used during
     * authentication.
     *
     * In this application, the email field from the local User entity is used
     * as the username identifier.
     */
    public String getUsername() {
        return this.email;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*
     * Overrides the getPassword() method defined in the UserDetails interface
     * from Spring Security.
     *
     * The User entity implements UserDetails, so it must provide its own
     * implementation of this method.
     *
     * Original method:
     * - UserDetails interface -> String getPassword();
     *
     * This overridden method returns the password field stored in this User entity.
     *
     * Spring Security uses this method during authentication to retrieve the
     * encrypted password from the database and compare it with the password
     * provided by the user during login.
     */
    @Override
    /*
     * getPassword() is a method defined by the Spring Security UserDetails interface.
     *
     * The User entity overrides this method to provide the password value required
     * by Spring Security during authentication.
     *
     * This method returns the password field from the local User entity, which
     * contains the encrypted password stored in the database.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Order01> getOrders() {
        return orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


/*
 * Java (Programming Language + JDK)
 *          │
 *          ▼
 * Spring Ecosystem
 *          │
 *          ├── Spring Framework
 *          │       │
 *          │       ├── Spring MVC
 *          │       │       → Used to build web applications and REST APIs
 *          │       │
 *          │       ├── Spring Data JPA
 *          │       │       → Simplifies database access using JPA repositories
 *          │       │
 *          │       ├── Spring Security
 *          │       │       → Provides authentication and authorization features
 *          │       │
 *          │       └── Other Spring modules
 *          │
 *          ▼
 * Spring Boot
 *          │
 *          └── Simplifies the configuration, dependency management, and
 *              execution of Spring applications through auto-configuration.
 *
 *
 * In this project, Spring Security was added through the dependency:
 *
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-security</artifactId>
 * </dependency>
 *
 * This dependency provides the Spring Security modules required to implement
 * authentication and authorization in the application.
 */