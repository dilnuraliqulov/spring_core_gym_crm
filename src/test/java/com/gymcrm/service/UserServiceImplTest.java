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


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void testFindByUsername_userExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        Optional<User>result = userServiceImpl.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals("test",result.get().getUsername());
        verify(userRepository,times(1)).findByUsername("test");

    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.findByUsername("unknown")
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }
    @Test
    void testAuthenticate_success() {
        char[] password = {'p','a','s','s','1','2','3','4'};
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(password);
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        boolean result = userServiceImpl.authenticate("testuser", password);

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testAuthenticate_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.authenticate("unknown", new char[]{})
        );

        assertEquals("User not foundunknown", exception.getMessage());
    }

    @Test
    void testAuthenticate_userInactive() {
        char[] password = {'p','a','s','s','1','2','3','4'};
        User user = new User();
        user.setUsername("inactiveUser");
        user.setPassword(password);
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
        char[] correctPassword = {'p','a','s','s','1','2','3','4'};
        char[] wrongPassword = {'w','r','o','n','g','1','2','3','4'};

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(correctPassword);
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userServiceImpl.authenticate("testuser", wrongPassword)
        );

        assertEquals("Invalid password for username: testuser", exception.getMessage());
    }

    @Test
    void testMatches_true() {
        char[] a = {'1','2','3','4'};
        char[] b = {'1','2','3','4'};
        assertTrue(userServiceImpl.matches(a, b));
    }

    @Test
    void testMatches_false() {
        char[] a = {'1','2','3','4'};
        char[] b = {'1','2','3','5'};
        assertFalse(userServiceImpl.matches(a, b));
    }


}
