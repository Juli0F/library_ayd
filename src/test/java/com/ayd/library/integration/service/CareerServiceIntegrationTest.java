package com.ayd.library.integration.service;

import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.repository.CareerRepository;
import com.ayd.library.service.CareerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CareerServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private CareerService careerService;

    @Autowired
    private CareerRepository careerRepository;

    private Career career;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        career = new Career();
        career.setCode("CS101");
        career.setName("Computer Science");
        career.setStatus(true);
    }

    @Test
    public void testCreateCareer() throws DuplicatedEntityException {
        // Act
        Career createdCareer = careerService.createCareer(career);

        // Assert
        assertNotNull(createdCareer);
        assertEquals(career.getCode(), createdCareer.getCode());
        assertEquals(career.getName(), createdCareer.getName());
    }

    @Test
    public void testCreateCareer_DuplicatedEntityException() {
        // Arrange
        careerRepository.save(career);

        // Act & Assert
        DuplicatedEntityException thrown = assertThrows(DuplicatedEntityException.class, () -> {
            careerService.createCareer(career);
        });

        assertEquals("Existe la carrera con el codigo: CS101", thrown.getMessage());
    }

    @Test
    public void testUpdateCareer() throws NotFoundException {
        // Arrange
        careerRepository.save(career);
        Career updatedCareer = new Career();
        updatedCareer.setName("Software Engineering");

        // Act
        Career result = careerService.updateCareer(career.getCode(), updatedCareer);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCareer.getName(), result.getName());
    }

    @Test
    public void testUpdateCareer_NotFoundException() {
        // Arrange
        Career updatedCareer = new Career();
        updatedCareer.setName("Software Engineering");

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            careerService.updateCareer("CS102", updatedCareer);
        });

        assertEquals("No se encontró la carrera con código: CS102", thrown.getMessage());
    }

    @Test
    public void testGetCareerByCode() throws NotFoundException {
        // Arrange
        careerRepository.save(career);

        // Act
        Career foundCareer = careerService.getCareerByCode(career.getCode());

        // Assert
        assertNotNull(foundCareer);
        assertEquals(career.getCode(), foundCareer.getCode());
    }

    @Test
    public void testGetCareerByCode_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            careerService.getCareerByCode("CS102");
        });

        assertEquals("No se encontró la carrera con código: CS102", thrown.getMessage());
    }

    @Test
    public void testGetAllActiveCareers() {
        // Arrange
        careerRepository.save(career);

        // Act
        List<Career> careers = careerService.getAllActiveCareers();

        // Assert
        assertNotNull(careers);
        assertFalse(careers.isEmpty());
        assertEquals(1, careers.size());
    }

    @Test
    public void testSoftDeleteCareer() throws NotFoundException {
        // Arrange
        careerRepository.save(career);

        // Act
        Career softDeletedCareer = careerService.softDeleteCareer(career.getCode());

        // Assert
        assertNotNull(softDeletedCareer);
        assertFalse(softDeletedCareer.getStatus());
    }

    @Test
    public void testSoftDeleteCareer_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            careerService.softDeleteCareer("CS102");
        });

        assertEquals("No se encontró la carrera con código: CS102", thrown.getMessage());
    }
}
