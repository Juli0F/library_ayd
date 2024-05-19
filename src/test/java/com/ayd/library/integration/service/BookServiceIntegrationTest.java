package com.ayd.library.integration.service;

import com.ayd.library.dto.BookRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.repository.BookRepository;
import com.ayd.library.service.BookService;
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
public class BookServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private BookRequestDto bookRequestDto;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        bookRequestDto = new BookRequestDto();
        bookRequestDto.setCode("B001");
        bookRequestDto.setTitle("Test Book");
        bookRequestDto.setAuthor("Test Author");
        bookRequestDto.setPublicationDate(LocalDate.now());
        bookRequestDto.setAvailableCopies(5);
        bookRequestDto.setPublisher("Test Publisher");
    }

    @Test
    public void testCreateBook() throws DuplicatedEntityException {
        // Act
        Book createdBook = bookService.createBook(bookRequestDto);

        // Assert
        assertNotNull(createdBook);
        assertEquals(bookRequestDto.getCode(), createdBook.getCode());
        assertEquals(bookRequestDto.getTitle(), createdBook.getTitle());
    }

    @Test
    public void testCreateBook_DuplicatedEntityException() throws DuplicatedEntityException {
        // Arrange
        bookService.createBook(bookRequestDto);

        // Act & Assert
        DuplicatedEntityException thrown = assertThrows(DuplicatedEntityException.class, () -> {
            bookService.createBook(bookRequestDto);
        });

        assertEquals("Existe un libro con el codigo: B001", thrown.getMessage());
    }

    @Test
    public void testUpdateBook() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        bookService.createBook(bookRequestDto);
        BookRequestDto updatedBookRequest = new BookRequestDto();
        updatedBookRequest.setTitle("Updated Book Title");
        updatedBookRequest.setAuthor("Updated Author");
        updatedBookRequest.setAvailableCopies(10);
        updatedBookRequest.setPublisher("Updated Publisher");

        // Act
        Book updatedBook = bookService.updateBook(bookRequestDto.getCode(), updatedBookRequest);

        // Assert
        assertNotNull(updatedBook);
        assertEquals(updatedBookRequest.getTitle(), updatedBook.getTitle());
    }

    @Test
    public void testUpdateBook_NotFoundException() {
        // Arrange
        BookRequestDto updatedBookRequest = new BookRequestDto();
        updatedBookRequest.setTitle("Updated Book Title");
        updatedBookRequest.setAuthor("Updated Author");
        updatedBookRequest.setAvailableCopies(10);
        updatedBookRequest.setPublisher("Updated Publisher");

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            bookService.updateBook("NonExistentCode", updatedBookRequest);
        });

        assertEquals("Book not found with ID: NonExistentCode", thrown.getMessage());
    }

    @Test
    public void testGetBookByCode() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        bookService.createBook(bookRequestDto);

        // Act
        Book foundBook = bookService.getBookByCode(bookRequestDto.getCode());

        // Assert
        assertNotNull(foundBook);
        assertEquals(bookRequestDto.getCode(), foundBook.getCode());
    }

    @Test
    public void testGetBookByCode_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            bookService.getBookByCode("NonExistentCode");
        });

        assertEquals("No se encuentra el libro con el codigo: NonExistentCode", thrown.getMessage());
    }

    @Test
    public void testGetAllBooks() throws DuplicatedEntityException {
        // Arrange
        bookService.createBook(bookRequestDto);

        // Act
        List<Book> books = bookService.getAllBooks();

        // Assert
        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }

    @Test
    public void testSoftDeleteBook() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        bookService.createBook(bookRequestDto);

        // Act
        Book softDeletedBook = bookService.softDeleteBook(bookRequestDto.getCode());

        // Assert
        assertNotNull(softDeletedBook);
        assertFalse(softDeletedBook.getStatus());
    }

    @Test
    public void testSoftDeleteBook_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            bookService.softDeleteBook("NonExistentCode");
        });

        assertEquals("No se encuentra el libro con el codigo: NonExistentCode", thrown.getMessage());
    }
}
