package com.gymcrm.service;

import com.gymcrm.entity.User;
import com.gymcrm.exception.UserNotFoundException;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void testFindByUsername_userExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals("test", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("test");
    }

    @Test
    void testFindByUsername_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.findByUsername("unknown")
        );

        assertEquals("User not found: unknown", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void testAuthenticate_success() {
        char[] password = "password123".toCharArray();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedPassword".toCharArray());
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        boolean result = userServiceImpl.authenticate("testuser", password);

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "hashedPassword");
    }

    @Test
    void testAuthenticate_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.authenticate("unknown", new char[]{})
        );

        assertEquals("User not found: unknown", exception.getMessage());
    }

    @Test
    void testAuthenticate_userInactive() {
        char[] password = "password123".toCharArray();
        User user = new User();
        user.setUsername("inactiveUser");
        user.setPassword("hashedPassword".toCharArray());
        user.setActive(false);

        when(userRepository.findByUsername("inactiveUser")).thenReturn(Optional.of(user));

        DisabledException exception = assertThrows(
                DisabledException.class,
                () -> userServiceImpl.authenticate("inactiveUser", password)
        );

        assertEquals("User inactiveUser is disabled", exception.getMessage());
    }

    @Test
    void testAuthenticate_wrongPassword() {
        char[] password = "password123".toCharArray();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedPassword".toCharArray());
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userServiceImpl.authenticate("testuser", password)
        );

        assertEquals("Invalid password for username: testuser", exception.getMessage());
    }

    @Test
    void testChangePassword_success() {
        char[] currentPassword = "oldPass".toCharArray();
        char[] newPassword = "newPass".toCharArray();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedOldPass".toCharArray());
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "hashedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");

        userServiceImpl.changePassword("testuser", currentPassword, newPassword);

        assertArrayEquals("hashedNewPass".toCharArray(), user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePassword_wrongCurrentPassword() {
        char[] currentPassword = "wrong".toCharArray();
        char[] newPassword = "newPass".toCharArray();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedOldPass".toCharArray());
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedOldPass")).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userServiceImpl.changePassword("testuser", currentPassword, newPassword)
        );

        assertEquals("Invalid current password for username: testuser", exception.getMessage());
    }

    @Test
    void testActivateDeactivate() {
        User user = new User();
        user.setActive(false);

        userServiceImpl.activate(user);
        assertTrue(user.isActive());

        userServiceImpl.deactivate(user);
        assertFalse(user.isActive());
    }
}
