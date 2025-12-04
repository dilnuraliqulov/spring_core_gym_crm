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

}
