package com.ayd.library.integration.service;

import com.ayd.library.dto.user.CredentialsDto;
import com.ayd.library.dto.user.UserRequestDto;
import com.ayd.library.enums.Rol;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.User;
import com.ayd.library.repository.UserRepository;
import com.ayd.library.service.AuthenticationService;
import com.ayd.library.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class AuthenticationServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    private UserRequestDto userRequestDto;
    private CredentialsDto credentialsDto;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {

        userRequestDto = new UserRequestDto("admin", "admin", "123", "admin@library.com", Rol.ADMIN.name().toUpperCase());
        credentialsDto = new CredentialsDto("admin", "123");
    }

    @Test
    public void testSignup() throws DuplicatedEntityException {
        // Act
        User createdUser = authenticationService.signup(userRequestDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(userRequestDto.username(), createdUser.getUsername());
        assertTrue(passwordEncoder.matches(userRequestDto.password(), createdUser.getPassword()));
    }

    @Test
    public void testSignup_DuplicatedEntityException() throws DuplicatedEntityException {
        // Arrange
        authenticationService.signup(userRequestDto);

        // Act & Assert
        DuplicatedEntityException thrown = assertThrows(DuplicatedEntityException.class, () -> {
            authenticationService.signup(userRequestDto);
        });

        assertEquals("User with username already exists", thrown.getMessage());
    }

    @Test
    public void testUpdateUser() throws ServiceException, DuplicatedEntityException {
        // Arrange
        User createdUser = authenticationService.signup(userRequestDto);
        UserRequestDto updatedUserRequest = new UserRequestDto("admin", "admin", "123", "admin@library.com", Rol.ADMIN.name().toUpperCase());

        // Act
        User updatedUser = authenticationService.updateUser(createdUser.getUserId(), updatedUserRequest);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUserRequest.email(), updatedUser.getEmail());
        assertEquals(updatedUserRequest.name(), updatedUser.getName());
    }

    @Test
    public void testUpdateUser_NotFoundException() {
        // Arrange
        UserRequestDto updatedUserRequest = new
                 UserRequestDto("testuser", "user-test", "password", "user-test@library.com", Rol.ADMIN.name().toUpperCase());

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            authenticationService.updateUser(999L, updatedUserRequest);
        });

        assertEquals("User not found", thrown.getMessage());
    }

//    @Test
//    public void testSignin() throws ServiceException, IOException, DuplicatedEntityException {
//        // Arrange
//        User createdUser = authenticationService.signup(userRequestDto);
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//        when(jwtService.generateToken(createdUser)).thenReturn("testtoken");
//
//        // Act
//        String token =authenticationService.signin(credentialsDto);
//
//        // Assert
//        assertNotNull(token);
//        assertEquals("testtoken", token);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService, times(1)).generateToken(createdUser);
//    }

    @Test
    public void testSignin_UserNotFoundException() {
        // Arrange
        CredentialsDto credentialsDto = new CredentialsDto("nonexistentuser", "password");

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            authenticationService.signin(credentialsDto);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void testSignin_InvalidCredentialsException() throws DuplicatedEntityException {
        // Arrange
        authenticationService.signup(userRequestDto);
        CredentialsDto wrongCredentials = new CredentialsDto("admin", "no-son-las-credenciales");

        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.signin(wrongCredentials);
        });

        assertEquals("Invalid user credentials", thrown.getMessage());
    }
}
