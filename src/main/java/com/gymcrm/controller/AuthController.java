package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.request.LoginRequest;
import com.gymcrm.dto.response.ErrorResponse;
import com.gymcrm.dto.response.JwtResponse;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.security.brute.BruteForceProtectionService;
import com.gymcrm.security.jwt.JwtBlacklistService;
import com.gymcrm.security.jwt.JwtService;
import com.gymcrm.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final BruteForceProtectionService bruteForceProtectionService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {

        if (bruteForceProtectionService.isBlocked(request.getUsername())) {
            throw new AuthenticationException("User temporarily blocked due to failed login attempts");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            bruteForceProtectionService.loginSucceeded(request.getUsername());

            return ResponseEntity.ok(
                    new JwtResponse(jwtService.generateToken(auth.getName()))
            );
        } catch (Exception ex) {
            bruteForceProtectionService.loginFailed(request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getName().equals(request.getUsername())) {
            throw new AuthenticationException("Access denied");
        }

        userService.changePassword(
                request.getUsername(),
                request.getOldPassword().toCharArray(),
                request.getNewPassword().toCharArray()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authHeader.substring(7);
        jwtBlacklistService.blacklistToken(token, 3600_000); // 1 hour expiry or read from JWT
        SecurityContextHolder.clearContext();

        log.info("User logged out successfully");
        return ResponseEntity.ok().build();
    }
}
