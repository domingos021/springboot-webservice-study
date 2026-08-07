package com.diniz.springbootstudy.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

// Marks this class as a JPA entity managed by Hibernate
@Entity
// Specifies the custom database table name ('tb_user' instead of default 'User')
@Table(name = "tb_user")
public class User implements Serializable {

    // Identifies the field used for class serialization compatibility
    @Serial
    private static final long serialVersionUID = 1L;

    // Marks this field as the primary key of the table
    @Id
    // Delegates ID generation to the database auto-increment feature
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String password;

    public User() {}

    public User(Long id, String email, String name, String phone, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Overrides default Object equals to compare entities by ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    // Overrides default Object hashCode based on the unique ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}