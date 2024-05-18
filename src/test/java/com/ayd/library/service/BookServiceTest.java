package com.ayd.library.service;

import com.ayd.library.dto.BookRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequestDto bookRequestDto;
    private Book book;


    @BeforeEach
    void setUp() {
        bookRequestDto = new BookRequestDto();
        bookRequestDto.setCode("123");
        bookRequestDto.setTitle("Test Title");
        bookRequestDto.setAuthor("Test Author");
        bookRequestDto.setPublicationDate(LocalDate.of(2021,01,01));
        bookRequestDto.setAvailableCopies(10);
        bookRequestDto.setPublisher("Test Publisher");

        book = Book.builder()
                .code("123")
                .title("Test Title")
                .author("Test Author")
                .publicationDate(LocalDate.of(2021,01,01))
                .availableCopies(10)
                .publisher("Test Publisher")
                .status(true)
                .build();
    }

    @Test
    void createBookTest() throws DuplicatedEntityException {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book createdBook = bookService.createBook(bookRequestDto);

        // Assert
        assertNotNull(createdBook);
        assertEquals(bookRequestDto.getCode(), createdBook.getCode());
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    @Test
    public void testCreateBook_DuplicatedEntityException() {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> bookService.createBook(bookRequestDto));
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(0)).save(any(Book.class));
    }


    @Test
    void updateBookTest() throws NotFoundException  {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book updatedBook = bookService.updateBook(bookRequestDto.getCode(), bookRequestDto);

        // Assert
        assertNotNull(updatedBook);
        assertEquals(bookRequestDto.getTitle(), updatedBook.getTitle());
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    @Test
    public void testUpdateBook_NotFoundException() {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookService.updateBook(bookRequestDto.getCode(), bookRequestDto));
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(0)).save(any(Book.class));
    }


    @Test
    void getBookByCodeTest() throws NotFoundException {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.of(book));

        // Act
        Book foundBook = bookService.getBookByCode(bookRequestDto.getCode());

        // Assert
        assertNotNull(foundBook);
        assertEquals(bookRequestDto.getCode(), foundBook.getCode());
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
    }
    @Test
    public void testGetBookByCode_NotFoundException() {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookService.getBookByCode(bookRequestDto.getCode()));
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
    }


    @Test
    void softDeleteBook() throws  NotFoundException {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book deletedBook = bookService.softDeleteBook(bookRequestDto.getCode());

        // Assert
        assertNotNull(deletedBook);
        assertFalse(deletedBook.getStatus());
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    @Test
    public void testSoftDeleteBook_NotFoundException() {
        // Arrange
        when(bookRepository.findById(bookRequestDto.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookService.softDeleteBook(bookRequestDto.getCode()));
        verify(bookRepository, times(1)).findById(bookRequestDto.getCode());
        verify(bookRepository, times(0)).save(any(Book.class));
    }
}