package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.response.ErrorResponse;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Login", description = "Validates user credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(
            @Parameter(description = "Username", required = true)
            @RequestParam String username,
            @Parameter(description = "Password", required = true)
            @RequestParam String password) {

        log.info("Login attempt for user: {}", username);

        if (!userService.authenticate(username, password.toCharArray())) {
            throw new AuthenticationException("Invalid username or password");
        }

        log.info("Login successful for user: {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password", description = "Changes user password (requires authentication with old password)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        log.info("Password change request for user: {}", request.getUsername());

        // Authenticate with old password first
        if (!userService.authenticate(request.getUsername(), request.getOldPassword().toCharArray())) {
            throw new AuthenticationException("Invalid username or old password");
        }

        userService.changePassword(
                request.getUsername(),
                request.getOldPassword().toCharArray(),
                request.getNewPassword().toCharArray()
        );

        log.info("Password changed successfully for user: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }
}

