package com.ayd.library.integration.service;

import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.enums.ReservationStatusEnum;
import com.ayd.library.exception.*;
import com.ayd.library.model.Book;
import com.ayd.library.model.Career;
import com.ayd.library.model.Reservation;
import com.ayd.library.model.Student;
import com.ayd.library.repository.BookRepository;
import com.ayd.library.repository.ReservationRepository;
import com.ayd.library.repository.StudentRepository;
import com.ayd.library.service.BookService;
import com.ayd.library.service.CareerService;
import com.ayd.library.service.ReservationService;
import com.ayd.library.service.StudentService;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class ReservationServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private BookService bookService;
    @Autowired
    private CareerService careerService;

    private ReservationRequestDto reservationRequestDto;
    private Student student;
    private Book book;
    private Career career;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    public void setUp() throws DuplicatedEntityException {
        career = Career.builder()
                .code("CS")
                .name("System")
                .status(true)
                .build();
        careerService.createCareer(career);

        student = new Student();
        student.setCarnet("ST001");
        student.setName("Julio Test Integration");
        student.setCareerCode(career);
        student.setBirthDate(LocalDate.of(1997, 1, 1));
        student.setStatus(true);
        studentRepository.save(student);

        book = new Book();
        book.setCode("B001");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublicationDate(LocalDate.now());
        book.setAvailableCopies(5);
        book.setPublisher("Test Publisher");
        book.setStatus(true);
        bookRepository.save(book);

        reservationRequestDto = new ReservationRequestDto();
        //reservationRequestDto.setId(1L);
        reservationRequestDto.setReservationDate(LocalDate.now());
        reservationRequestDto.setBookCode("B001");
        reservationRequestDto.setCarnet("ST001");
        reservationRequestDto.setStatus(ReservationStatusEnum.ACTIVE.name().toUpperCase());
    }

    @Test
    public void testCreateReservation() throws DuplicatedEntityException, NotFoundException, RequiredEntityException, EnoughException {
        // Act
        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);

        // Assert
        assertNotNull(createdReservation);
        assertEquals(reservationRequestDto.getReservationDate(), createdReservation.getReservationDate());
        assertEquals(ReservationStatusEnum.ACTIVE.name(), createdReservation.getStatus());
    }


    @Test
    public void testCreateReservation_EnoughException() {
        // Arrange
        book.setAvailableCopies(0);
        bookRepository.save(book);

        // Act & Assert
        EnoughException thrown = assertThrows(EnoughException.class, () -> {
            reservationService.createReservation(reservationRequestDto);
        });

        assertEquals("Not enough available copies", thrown.getMessage());
    }

    @Test
    public void testCreateReservation_RequiredEntityException() {
        // Arrange
        reservationRequestDto.setBookCode(null);

        // Act & Assert
        RequiredEntityException thrown = assertThrows(RequiredEntityException.class, () -> {
            reservationService.createReservation(reservationRequestDto);
        });

        assertEquals("Book code must not be null", thrown.getMessage());
    }

    @Test
    public void testUpdateReservation() throws NotFoundException, DuplicatedEntityException, RequiredEntityException, EnoughException {
        // Arrange
        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);
        Reservation updatedReservation = new Reservation();
        updatedReservation.setReservationDate(LocalDate.now().plusDays(1));
        updatedReservation.setStatus(ReservationStatusEnum.COMPLETED.name());
        updatedReservation.setStudent(student);
        updatedReservation.setBookCode(book);

        // Act
        Reservation result = reservationService.updateReservation(createdReservation.getId(), updatedReservation);

        // Assert
        assertNotNull(result);
        assertEquals(updatedReservation.getReservationDate(), result.getReservationDate());
        assertEquals(updatedReservation.getStatus(), result.getStatus());
    }

    @Test
    public void testUpdateReservation_NotFoundException() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setReservationDate(LocalDate.now().plusDays(1));
        updatedReservation.setStatus(ReservationStatusEnum.COMPLETED.name());
        updatedReservation.setStudent(student);
        updatedReservation.setBookCode(book);

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            reservationService.updateReservation(999L, updatedReservation);
        });

        assertEquals("Reservation not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testGetReservationById() throws NotFoundException, DuplicatedEntityException, RequiredEntityException, EnoughException {
        // Arrange
        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);

        // Act
        Reservation foundReservation = reservationService.getReservationById(createdReservation.getId());

        // Assert
        assertNotNull(foundReservation);
        assertEquals(createdReservation.getId(), foundReservation.getId());
    }

    @Test
    public void testGetReservationById_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            reservationService.getReservationById(999L);
        });

        assertEquals("Reservation not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testGetAllReservations() throws DuplicatedEntityException, NotFoundException, RequiredEntityException, EnoughException {
        // Arrange
        reservationService.createReservation(reservationRequestDto);

        // Act
        List<Reservation> reservations = reservationService.getAllReservations();

        // Assert
        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
    }

    @Test
    public void testUpdateReservationStatus() throws NotFoundException, DuplicatedEntityException, RequiredEntityException, EnoughException {
        // Arrange
        reservationRequestDto.setId(1L);
        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);

        // Act
        Reservation updatedReservation = reservationService.updateReservationStatus(createdReservation.getId(), ReservationStatusEnum.COMPLETED.name());

        // Assert
        assertNotNull(updatedReservation);
        assertEquals(ReservationStatusEnum.COMPLETED.name().toUpperCase(), updatedReservation.getStatus().toUpperCase());
    }

    @Test
    public void testUpdateReservationStatus_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            reservationService.updateReservationStatus(999L, ReservationStatusEnum.COMPLETED.name());
        });

        assertEquals("Reservation not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testFindReservationsByStatus() throws DuplicatedEntityException, NotFoundException, RequiredEntityException, EnoughException {
        // Arrange
        reservationService.createReservation(reservationRequestDto);

        // Act
        List<Reservation> reservations = reservationService.findReservationsByStatus(ReservationStatusEnum.ACTIVE.name());

        // Assert
        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
    }
}
