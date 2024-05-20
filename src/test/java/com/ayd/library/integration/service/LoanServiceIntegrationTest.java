package com.ayd.library.integration.service;

import com.ayd.library.dto.LoanRequestDto;
import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.enums.LoanStatus;
import com.ayd.library.exception.*;
import com.ayd.library.model.Book;
import com.ayd.library.model.Career;
import com.ayd.library.model.Loan;
import com.ayd.library.model.Student;
import com.ayd.library.repository.BookRepository;
import com.ayd.library.repository.LoanRepository;
import com.ayd.library.repository.StudentRepository;
import com.ayd.library.service.BookService;
import com.ayd.library.service.CareerService;
import com.ayd.library.service.LoanService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class LoanServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;
    @Autowired
    private CareerService careerService;

    @Autowired
    private BookService bookService;

    private LoanRequestDto loanRequestDto;
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
        student.setCareerCode(career);
        student.setName("Julio Test");
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

        loanRequestDto = new LoanRequestDto();
        loanRequestDto.setId(1L);
        loanRequestDto.setLoanDate(LocalDate.now());
        loanRequestDto.setReturnDate(LocalDate.now().plusDays(7));
        loanRequestDto.setBookCode("B001");
        loanRequestDto.setCarnet("ST001");
        loanRequestDto.setTotalDue(BigDecimal.valueOf(100));
    }

    @Test
    public void testCreateLoan() throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
        // Act
        Loan createdLoan = loanService.createLoan(loanRequestDto);

        // Assert
        assertNotNull(createdLoan);
        assertEquals(loanRequestDto.getLoanDate(), createdLoan.getLoanDate());
        assertEquals(loanRequestDto.getReturnDate(), createdLoan.getReturnDate());
        assertEquals(loanRequestDto.getTotalDue(), createdLoan.getTotalDue());
        assertEquals(LoanStatus.ACTIVE.name(), createdLoan.getStatus());
    }

//    @Test
//    public void testCreateLoan_DuplicatedEntityException() throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
//        // Arrange
//        loanService.createLoan(loanRequestDto);
//
//        // Act & Assert
//        DuplicatedEntityException thrown = assertThrows(DuplicatedEntityException.class, () -> {
//            loanService.createLoan(loanRequestDto);
//        });
//
//        assertEquals("Loan with ID already exists: 1", thrown.getMessage());
//    }

    @Test
    public void testCreateLoan_EnoughException() {
        // Arrange
        book.setAvailableCopies(0);
        bookRepository.save(book);

        // Act & Assert
        EnoughException thrown = assertThrows(EnoughException.class, () -> {
            loanService.createLoan(loanRequestDto);
        });

        assertEquals("Enough available copies", thrown.getMessage());
    }

    @Test
    public void testCreateLoan_QuantityException() {
        // Arrange
        loanRequestDto.setTotalDue(BigDecimal.valueOf(-100));

        // Act & Assert
        QuantityException thrown = assertThrows(QuantityException.class, () -> {
            loanService.createLoan(loanRequestDto);
        });

        assertEquals("Cannot create loan without total due: -100", thrown.getMessage());
    }

//    @Test
//    public void testUpdateLoan() throws NotFoundException, DuplicatedEntityException, QuantityException, RequiredEntityException, EnoughException {
//        // Arrange
//        Loan createdLoan = loanService.createLoan(loanRequestDto);
//        Loan updatedLoan = new Loan();
//        updatedLoan.setLoanDate(LocalDate.now().plusDays(1));
//        updatedLoan.setReturnDate(LocalDate.now().plusDays(8));
//        updatedLoan.setTotalDue(BigDecimal.valueOf(200));
//        updatedLoan.setStatus(LoanStatus.RETURNED.name());
//        updatedLoan.setStudent(student);
//        updatedLoan.setBookCode(book);
//
//        // Act
//        Loan result = loanService.updateLoan(createdLoan.getId(), updatedLoan);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(updatedLoan.getLoanDate(), result.getLoanDate());
//        assertEquals(updatedLoan.getReturnDate(), result.getReturnDate());
//        assertEquals(updatedLoan.getTotalDue(), result.getTotalDue());
//        assertEquals(updatedLoan.getStatus(), result.getStatus());
//    }

    @Test
    public void testUpdateLoan_NotFoundException() {
        // Arrange
        Loan updatedLoan = new Loan();
        updatedLoan.setLoanDate(LocalDate.now().plusDays(1));
        updatedLoan.setReturnDate(LocalDate.now().plusDays(8));
        updatedLoan.setTotalDue(BigDecimal.valueOf(200));
        updatedLoan.setStatus(LoanStatus.RETURNED.name());
        updatedLoan.setStudent(student);
        updatedLoan.setBookCode(book);

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            loanService.updateLoan(999L, updatedLoan);
        });

        assertEquals("Loan not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testGetLoanById() throws NotFoundException, DuplicatedEntityException, QuantityException, RequiredEntityException, EnoughException {
        // Arrange
        Loan createdLoan = loanService.createLoan(loanRequestDto);

        // Act
        Loan foundLoan = loanService.getLoanById(createdLoan.getId());

        // Assert
        assertNotNull(foundLoan);
        assertEquals(createdLoan.getId(), foundLoan.getId());
    }

    @Test
    public void testGetLoanById_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            loanService.getLoanById(999L);
        });

        assertEquals("Loan not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testGetAllLoans() throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
        // Arrange
        loanService.createLoan(loanRequestDto);

        // Act
        List<LoanResponseDto> loans = loanService.getAllLoans();

        // Assert
        assertNotNull(loans);
        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
    }

    @Test
    public void testCloseLoan() throws NotFoundException, DuplicatedEntityException, QuantityException, RequiredEntityException, EnoughException {
        // Arrange
        Loan createdLoan = loanService.createLoan(loanRequestDto);

        // Act
        Loan closedLoan = loanService.closeLoan(createdLoan.getId());

        // Assert
        assertNotNull(closedLoan);
        assertEquals("returned", closedLoan.getStatus());
    }

    @Test
    public void testCloseLoan_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            loanService.closeLoan(999L);
        });

        assertEquals("Loan not found with ID: 999", thrown.getMessage());
    }

    @Test
    public void testFindLoansByStatus() throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
        // Arrange
        loanService.createLoan(loanRequestDto);

        // Act
        List<Loan> loans = loanService.findLoansByStatus(LoanStatus.ACTIVE.name());

        // Assert
        assertNotNull(loans);
        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
    }
}
