package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.request.LoginRequest;
import com.gymcrm.dto.response.JwtResponse;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.security.brute.BruteForceProtectionService;
import com.gymcrm.security.jwt.JwtBlacklistService;
import com.gymcrm.security.jwt.JwtService;
import com.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private BruteForceProtectionService bruteForceProtectionService;

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");

        when(bruteForceProtectionService.isBlocked("user")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(jwtService.generateToken("user")).thenReturn("jwt-token");

        ResponseEntity<JwtResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt-token", response.getBody().getToken());

        verify(bruteForceProtectionService).loginSucceeded("user");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_Failure_Blocked() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");

        when(bruteForceProtectionService.isBlocked("user")).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authController.login(request));
        verify(bruteForceProtectionService, never()).loginSucceeded(anyString());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_Failure_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrong");

        when(bruteForceProtectionService.isBlocked("user")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(AuthenticationException.class, () -> authController.login(request));
        verify(bruteForceProtectionService).loginFailed("user");
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");

        ResponseEntity<Void> response = authController.changePassword(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).changePassword("user", "old".toCharArray(), "new".toCharArray());
    }

    @Test
    void changePassword_AccessDenied() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other-user");

        assertThrows(AuthenticationException.class, () -> authController.changePassword(request));
        verify(userService, never()).changePassword(anyString(), any(), any());
    }

    @Test
    void logout_Success() {
        String token = "Bearer jwt-token";

        ResponseEntity<Void> response = authController.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        verify(jwtBlacklistService).blacklistToken("jwt-token", 3600_000);
    }

    @Test
    void logout_BadRequest() {
        String token = "invalid-token";

        ResponseEntity<Void> response = authController.logout(token);

        assertEquals(400, response.getStatusCodeValue());
        verify(jwtBlacklistService, never()).blacklistToken(anyString(), anyLong());
    }
}
