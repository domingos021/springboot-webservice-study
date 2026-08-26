package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.AuthenticationDTO;
import com.diniz.springbootstudy.dto.ForgotPasswordDTO;
import com.diniz.springbootstudy.dto.LoginResponseDTO;
import com.diniz.springbootstudy.dto.ResetPasswordDTO;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.services.security.PasswordResetService;
import com.diniz.springbootstudy.services.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ============================================================================
// AUTHENTICATION CONTROLLER
// ============================================================================
// Handles authentication endpoints (/auth/login) and password recovery workflows
// (/auth/forgot-password and /auth/reset-password).
// ============================================================================

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;

    /**
     * Dependency injection via constructor.
     */
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            PasswordResetService passwordResetService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordResetService = passwordResetService;
    }

    /*
     * Endpoint: POST /auth/login
     * Receives email and password, authenticates against BCrypt password hash,
     * and returns an encoded JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {

        /*
         * Encapsulates the unauthenticated user credentials.
         */
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        /*
         * AuthenticationManager delegates password comparison to BCrypt.
         * If invalid, throws BadCredentialsException (HTTP 401/403).
         */
        var auth = this.authenticationManager.authenticate(usernamePassword);

        /*
         * Generates the JWT token using the authenticated User details.
         */
        var token = tokenService.generateToken((User) auth.getPrincipal());

        /*
         * Returns HTTP 200 OK with the token in body.
         */
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // ========================================================================
    // PASSWORD RECOVERY ENDPOINTS
    // ========================================================================

    /*
     * Endpoint: POST /auth/forgot-password
     * Receives the target account email, generates a 30-minute UUID reset token,
     * and dispatches a recovery email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
        passwordResetService.createPasswordResetToken(dto);
        return ResponseEntity.noContent().build();
    }

    /*
     * Endpoint: POST /auth/reset-password
     * Validates the provided reset token and updates the user password using BCrypt.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }
}