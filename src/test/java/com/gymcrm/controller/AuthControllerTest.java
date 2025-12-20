package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        String username = "user";
        String password = "pass";

        when(userService.authenticate(username, password.toCharArray())).thenReturn(true);

        ResponseEntity<Void> response = authController.login(username, password);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).authenticate(username, password.toCharArray());
    }

    @Test
    void login_Failure() {
        String username = "user";
        String password = "wrong";

        when(userService.authenticate(username, password.toCharArray())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authController.login(username, password));
        verify(userService).authenticate(username, password.toCharArray());
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(userService.authenticate("user", "old".toCharArray())).thenReturn(true);

        ResponseEntity<Void> response = authController.changePassword(request);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).changePassword("user", "old".toCharArray(), "new".toCharArray());
    }

    @Test
    void changePassword_InvalidOldPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("user");
        request.setOldPassword("wrong");
        request.setNewPassword("new");

        when(userService.authenticate("user", "wrong".toCharArray())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authController.changePassword(request));
        verify(userService, never()).changePassword(anyString(), any(), any());
    }
}
