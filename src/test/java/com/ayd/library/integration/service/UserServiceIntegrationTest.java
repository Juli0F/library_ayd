package com.ayd.library.integration.service;

import com.ayd.library.enums.Rol;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.User;
import com.ayd.library.repository.UserRepository;
import com.ayd.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    private User testUser;

    @BeforeEach
    public void setUp() {
//        testUser = new User();
//        testUser.setUsername("userTest");
//        testUser.setPassword("password");
//        testUser.setEmail("user-test@library.com");
        testUser = User.builder()
                .name("user")
                .username("user")
                .password("password")
                .email("user@email.com")
                .status((short)1)
                .role(Rol.ADMIN)
                .build();
        userRepository.save(testUser);
    }

    @Test
    public void testCreateUser() {
        // Act
        User createdUser = userService.createUser("julio","JulioNewUser", "NewPassword", "julio-New@library.com", Rol.ADMIN);

        // Assert
        assertNotNull(createdUser);
        assertEquals("JulioNewUser", createdUser.getUsername());
        assertTrue(userService.userDetailsService().loadUserByUsername("JulioNewUser").isEnabled());
    }

    @Test
    public void testFindById() throws ServiceException {
        // Act
        User foundUser = userService.findById(testUser.getUserId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    public void testFindById_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            userService.findById(999L);
        });

        assertEquals("This user sale with id:999 dont exists", thrown.getMessage());
    }

    @Test
    public void testFindByUsername() throws ServiceException {
        // Act
        User foundUser = userService.findByUsername(testUser.getUsername());

        // Assert
        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    public void testFindByUsername_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            userService.findByUsername("nonexistentuser");
        });

        assertEquals("This user sale with username:nonexistentuser dont exists", thrown.getMessage());
    }

    @Test
    public void testUserDetailsService() {
        // Act
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(testUser.getUsername());

        // Assert
        assertNotNull(userDetails);
        assertEquals(testUser.getUsername(), userDetails.getUsername());
    }

    @Test
    public void testUserDetailsService_UserNotFoundException() {
        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            userService.userDetailsService().loadUserByUsername("nonexistentuser");
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    public void testEncodeExistingPasswords() {
        // Act
        userService.encodeExistingPasswords();

        // Assert
        List<User> users = userRepository.findAll();
        for (User user : users) {
            assertTrue(user.getPassword().startsWith("$2a$"));
        }
    }
}
