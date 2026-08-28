package com.diniz.springbootstudy.entities;

import com.diniz.springbootstudy.entities.enums.UserRole;
import com.diniz.springbootstudy.entities.exemp.Order;
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
     * Persistent UserRole enum field stored as String in database.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role; // Type of user(client/admin)

    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

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
     * Converts UserRole enum into GrantedAuthority collections.
     * If ADMIN, assigns both ROLE_ADMIN and ROLE_CLIENT authorities.
     * If CLIENT, assigns only ROLE_CLIENT authority.
     */

    /*
     * Retorna as permissões (authorities) do usuário para controle de acesso do Spring Security.
     * Se for ADMIN, concede acesso total (ROLE_ADMIN e ROLE_CLIENT). Se for CLIENT, apenas ROLE_CLIENT.
     */
    /*
     * the name of this method: getAuthorities()
     * GrantedAuthority: Interface do Spring Security que representa uma permissão ou papel (Role)
     * concedido ao usuário. O Spring a utiliza para autorizar o acesso às rotas da API
     * (ex: .hasRole("ADMIN") busca por "ROLE_ADMIN").
     */
    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            //if admin
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_CLIENT")
            );
        }
        //if client
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    /*
     * Uses email as the principal username for authentication.
     */
    @Override
    @NonNull
    public String getUsername() {
        return this.email;
    }

    /*
     * ====================================================================================
     * NOTA SOBRE MÉTODOS DE STATUS DA CONTA (DELEÇÃO / REMOÇÃO DE OVERRIDE):
     * ====================================================================================
     * Os métodos a seguir (isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired,
     * e isEnabled) foram comentados/removidos pois a interface UserDetails do Spring Security 6+
     * já possui implementações 'default' que retornam 'true' automaticamente.
     *
     * Mantê-los reescritos apenas para retornar 'true' gerava avisos de código redundante na IDE
     * (RedundantMethodOverride). Ao não sobrescrevê-los, seguimos a boa prática do Spring Security,
     * mantendo o código mais limpo (DRY) e deixando a interface pai gerenciar o comportamento padrão.
     * ====================================================================================
     */

    /*
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    */

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

    @Override
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

    public List<Order> getOrders() {
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