package com.ayd.library.unit.controller;

import com.ayd.library.controller.BookController;
import com.ayd.library.dto.BookRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.service.BookService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testCreateBook() throws Exception {
        // Arrange
        BookRequestDto bookRequestDto = new BookRequestDto("B001", "Test Title", "Test Author", LocalDate.now(), "Test Publisher", 5);
        Book book = Book.builder()
                .code("B001")
                .title("El Dragon")
                .author("The last dragon")
                .publisher("The last dragon")
                .availableCopies(5)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();
        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(book);
        String content = """
                {
                    "code": "%s",
                     "title": "%s",
                     "author": "%s",
                     "publicationDate":"%s",
                     "publisher": "%s",
                     "availableCopies": %d
                  }
                """.formatted(book.getCode(), book.getTitle(), book.getAuthor(), book.getPublicationDate(),book.getPublisher() ,book.getAvailableCopies());
        // Act & Assert
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B001"))
                .andExpect(jsonPath("$.title").value("El Dragon"))
                .andExpect(jsonPath("$.author").value("The last dragon"))
//                .andExpect(jsonPath("$.publicationDate").value("2024-05-19"))
                .andExpect(jsonPath("$.publisher").value("The last dragon"))
                .andExpect(jsonPath("$.availableCopies").value(5));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetBookByCode() throws Exception {
        // Arrange
        Book book = Book.builder()
                .code("B001")
                .title("El Dragon")
                .author("The last dragon")
                .publisher("The last dragon")
                .availableCopies(5)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();
        when(bookService.getBookByCode("B001")).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/books/B001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B001"))
                .andExpect(jsonPath("$.title").value("El Dragon"))
                .andExpect(jsonPath("$.author").value("The last dragon"))
//                .andExpect(jsonPath("$.publicationDate").value("2024-05-19"))
                .andExpect(jsonPath("$.publisher").value("The last dragon"))
                .andExpect(jsonPath("$.availableCopies").value(5));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetAllBooks() throws Exception {
        // Arrange
        Book book = Book.builder()
                .code("B001")
                .title("El Dragon")
                .author("The last dragon")
                .publisher("The last dragon")
                .availableCopies(5)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();

        when(bookService.getAllBooks()).thenReturn(Collections.singletonList(book));

        // Act & Assert
        mockMvc.perform(get("/books/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("B001"))
                .andExpect(jsonPath("$[0].title").value("El Dragon"))
                .andExpect(jsonPath("$[0].author").value("The last dragon"))
//                .andExpect(jsonPath("$.publicationDate").value("2024-5-19"))
                .andExpect(jsonPath("$[0].publisher").value("The last dragon"))
                .andExpect(jsonPath("$[0].availableCopies").value(5));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testUpdateBook() throws Exception {
        // Arrange
        Book updatedBook = Book.builder()
                .code("B001")
                .title("El Dragon Actualizado")
                .author("The last dragon Actualizado")
                .publisher("The last dragon Actualizado")
                .availableCopies(10)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();

        when(bookService.updateBook(any(String.class), any(BookRequestDto.class))).thenReturn(updatedBook);

        String content = """
                {
                    "code": "%s",
                     "title": "%s",
                     "author": "%s",
                     "publicationDate":"%s",
                     "publisher": "%s",
                     "availableCopies": %d
                  }
                """.formatted(updatedBook.getCode(), updatedBook.getTitle(), updatedBook.getAuthor(), updatedBook.getPublicationDate(),updatedBook.getPublisher() ,updatedBook.getAvailableCopies());

        // Act & Assert
        mockMvc.perform(put("/books/B001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B001"))
                .andExpect(jsonPath("$.title").value("El Dragon Actualizado"))
                .andExpect(jsonPath("$.author").value("The last dragon Actualizado"))
//                .andExpect(jsonPath("$.publicationDate").value("2024-05-19"))
                .andExpect(jsonPath("$.publisher").value("The last dragon Actualizado"))
                .andExpect(jsonPath("$.availableCopies").value(10));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testDeleteBook() throws Exception {
        // Arrange
        Book book = Book.builder()
                .code("B001")
                .title("El Dragon")
                .author("The last dragon")
                .publisher("The last dragon")
                .availableCopies(10)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();

        when(bookService.softDeleteBook("B001")).thenReturn(book);

        // Act & Assert
        mockMvc.perform(delete("/books/B001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B001"))
                .andExpect(jsonPath("$.title").value("El Dragon"))
                .andExpect(jsonPath("$.author").value("The last dragon"))
//                .andExpect(jsonPath("$.publicationDate").value("2024-05-19"))
                .andExpect(jsonPath("$.publisher").value("The last dragon"));
                //.andExpect(jsonPath("$.status").value(Boolean.FALSE));

    }
}
