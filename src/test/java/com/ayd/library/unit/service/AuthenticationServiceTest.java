package com.ayd.library.unit.service;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRequestDto userRequestDto;
    private User user;
    private CredentialsDto credentials;

    @BeforeEach
    public void setUp() {
        userRequestDto = new UserRequestDto("Julio Test", "admin", "123", "julio_test@library.com", Rol.ADMIN.name().toUpperCase());
        user = new User();
        user.setUserId(1L);
        user.setUsername("admin");
        user.setEmail("julio_test@library.com");
        user.setName("Julio Test");
        user.setPassword("123");
        user.setStatus((short)1);

        credentials = new CredentialsDto("admin", "123");
    }

    @Test
    public void testSignup() throws DuplicatedEntityException {
        // Arrange
        when(userRepository.findByUsername(userRequestDto.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.empty());
        when(encoder.encode(userRequestDto.password())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = authenticationService.signup(userRequestDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(userRequestDto.username(), createdUser.getUsername());
        verify(userRepository, times(1)).findByUsername(userRequestDto.username());
        verify(userRepository, times(1)).findByEmail(userRequestDto.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSignup_DuplicatedUsernameException() {
        // Arrange
        when(userRepository.findByUsername(userRequestDto.username())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> authenticationService.signup(userRequestDto));
        verify(userRepository, times(1)).findByUsername(userRequestDto.username());
        verify(userRepository, times(0)).findByEmail(userRequestDto.email());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testSignup_DuplicatedEmailException() {
        // Arrange
        when(userRepository.findByUsername(userRequestDto.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> authenticationService.signup(userRequestDto));
        verify(userRepository, times(1)).findByUsername(userRequestDto.username());
        verify(userRepository, times(1)).findByEmail(userRequestDto.email());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUpdateUser() throws ServiceException {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndUserIdNot(userRequestDto.email(), user.getUserId())).thenReturn(Optional.empty());
        when(encoder.encode(userRequestDto.password())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = authenticationService.updateUser(user.getUserId(), userRequestDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(userRequestDto.username(), updatedUser.getUsername());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId());
        verify(userRepository, times(1)).findByEmailAndUserIdNot(userRequestDto.email(), user.getUserId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_NotFoundException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.updateUser(user.getUserId(), userRequestDto));
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(0)).findByUsernameAndUserIdNot(anyString(), anyLong());
        verify(userRepository, times(0)).findByEmailAndUserIdNot(anyString(), anyLong());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_DuplicatedUsernameException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> authenticationService.updateUser(user.getUserId(), userRequestDto));
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId());
        verify(userRepository, times(0)).findByEmailAndUserIdNot(anyString(), anyLong());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_DuplicatedEmailException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndUserIdNot(userRequestDto.email(), user.getUserId())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> authenticationService.updateUser(user.getUserId(), userRequestDto));
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).findByUsernameAndUserIdNot(userRequestDto.username(), user.getUserId());
        verify(userRepository, times(1)).findByEmailAndUserIdNot(userRequestDto.email(), user.getUserId());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testSignin() throws ServiceException, IOException {
        // Arrange
        when(userRepository.findByUsername(credentials.username())).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(user)).thenReturn("token");

        // Act
        String token = authenticationService.signin(credentials);

        // Assert
        assertNotNull(token);
        assertEquals("token", token);
        verify(userRepository, times(1)).findByUsername(credentials.username());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void testSignin_UserNotFoundException() throws IOException {
        // Arrange
        when(userRepository.findByUsername(credentials.username())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.signin(credentials));
        verify(userRepository, times(1)).findByUsername(credentials.username());
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    @Test
    public void testSignin_UserDisabledException() throws IOException {
        // Arrange
        user.setStatus((short)0);
        when(userRepository.findByUsername(credentials.username())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(ServiceException.class, () -> authenticationService.signin(credentials));
        verify(userRepository, times(1)).findByUsername(credentials.username());
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(0)).generateToken(any(User.class));
    }
    @Test
    public void testSignin_InvalidCredentialsException() throws IOException {
        // Arrange
        when(userRepository.findByUsername(credentials.username())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Invalid credentials") {});

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.signin(credentials));
        verify(userRepository, times(1)).findByUsername(credentials.username());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(0)).generateToken(any(User.class));
    }


//    @Test
//    public void testSignin_InvalidCredentialsException() throws IOException {
//        // Arrange
//        when(userRepository.findByUsername(credentials.username())).thenReturn(Optional.of(user));
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)));
//                //.thenThrow(new UsernamePasswordAuthenticationToken("invalid", "credentials"));
//
//        // Act & Assert
//        assertThrows(UsernameNotFoundException.class, () -> authenticationService.signin(credentials));
//        verify(userRepository, times(1)).findByUsername(credentials.username());
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService, times(0)).generateToken(any(User.class));
//    }
}
