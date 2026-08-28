package com.diniz.springbootstudy.config;

// ============================================================================
// API ROUTES CENTRAL MAPPER
// ============================================================================

public final class ApiRoutes {

    private ApiRoutes() {
    }

    // AUTH
    public static final String AUTH_LOGIN = "/auth/login";
    public static final String AUTH_FORGOT_PASSWORD = "/auth/forgot-password";
    public static final String AUTH_RESET_PASSWORD = "/auth/reset-password";

    // PUBLIC SHOWCASE
    public static final String PRODUCTS_BASE = "/products";
    public static final String PRODUCTS_WILDCARD = "/products/**";

    public static final String CATEGORIES_BASE = "/categories";
    public static final String CATEGORIES_WILDCARD = "/categories/**";

    // USERS
    public static final String USERS_BASE = "/users";
    public static final String USERS_BY_ID = "/users/{id}";
    public static final String USERS_WILDCARD = "/users/**";
    public static final String USERS_ME = "/users/me";

    // ORDERS - BASE LIMPA SEM '**'
    public static final String ORDERS_BASE = "/orders01";
    public static final String ORDERS_WILDCARD = "/orders01/**";

    // ADMIN
    public static final String ADMIN_BASE = "/admin/**";

    // SYSTEM
    public static final String H2_CONSOLE = "/h2-console/**";
    public static final String TEST_RESET = "/test/**";
}