package com.ayd.library.unit.controller;

import com.ayd.library.controller.LoanController;
import com.ayd.library.dto.LoanRequestDto;
import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.enums.LoanStatus;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.model.Loan;
import com.ayd.library.model.Student;
import com.ayd.library.service.LoanService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private Loan loan;
    private Book book;
    private Student student;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();

        book = Book.builder()
                .code("B001")
                .title("El Dragon")
                .author("The last dragon")
                .publisher("The last dragon")
                .availableCopies(5)
                .publicationDate(LocalDate.now())
                .status(Boolean.TRUE)
                .build();

        student = Student.builder()
                .carnet("S001")
                .name("Julio Test")
                .birthDate(LocalDate.of(1995, 1, 1))
                .status(true)
                .build();

        loan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(3))
                .bookCode(book)
                .student(student)
                .totalDue(BigDecimal.valueOf(100))
                .status("ACTIVE")
                .build();
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testCreateLoan() throws Exception {
        // Arrange
        when(loanService.createLoan(any(LoanRequestDto.class))).thenReturn(loan);

        String content = """
                {
                    "id": %d,
                    "loanDate": "%s",
                    "returnDate": "%s",
                    "bookCode": "%s",
                    "carnet": "%s",
                    "totalDue": %d
                }
                """.formatted(loan.getId(), loan.getLoanDate(), loan.getReturnDate(), loan.getBookCode().getCode(), loan.getStudent().getCarnet(), loan.getTotalDue().intValue());

        // Act & Assert
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loan.getId()))
                .andExpect(jsonPath("$.loanDate").value(loan.getLoanDate().toString()))
                .andExpect(jsonPath("$.returnDate").value(loan.getReturnDate().toString()))
//                .andExpect(jsonPath("$.bookCode.code").value(loan.getBookCode().getCode()))
//                .andExpect(jsonPath("$.student.carnet").value(loan.getStudent().getCarnet()))
                .andExpect(jsonPath("$.totalDue").value(loan.getTotalDue().intValue()))
                .andExpect(jsonPath("$.status").value(loan.getStatus()));
    }

//    @Test
//    @WithMockUser(authorities = "LIBRARIAN")
//    public void testGetLoanById() throws Exception {
//        // Arrange
//
//        when(loanService.getLoanById(1L)).thenReturn(loan);
//
//        // Act & Assert
//        mockMvc.perform(get("/loans/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(loan.getId()))
//                .andExpect(jsonPath("$.loanDate").value("2024-05-19"))
//                .andExpect(jsonPath("$.returnDate").value("2024-05-22"))
//                .andExpect(jsonPath("$.bookCode.code").value(loan.getBookCode().getCode()))
//                .andExpect(jsonPath("$.student.carnet").value(loan.getStudent().getCarnet()))
//                .andExpect(jsonPath("$.totalDue").value(loan.getTotalDue().intValue()))
//                .andExpect(jsonPath("$.status").value(loan.getStatus()));
//    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetLoanById() throws Exception {
        // Arrange
        Book book = new Book();
        book.setCode("B001");

        Student student = new Student();
        student.setCarnet("C123");

        Loan loan = Loan.builder()
                .id(1L)
                .bookCode(book)
                .student(student)
                .totalDue(BigDecimal.valueOf(100.0))
                .status("Active")
                .loanDate(LocalDate.of(2024, 5, 19))
                .returnDate(LocalDate.of(2024, 5, 22))
                .build();



        when(loanService.getLoanById(1L)).thenReturn(loan);

        // Act
        MvcResult result = mockMvc.perform(get("/loans/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Print the JSON response
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        // Assert
        mockMvc.perform(get("/loans/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loan.getId()))
                .andExpect(jsonPath("$.loanDate").value("2024-05-19"))
                .andExpect(jsonPath("$.returnDate").value("2024-05-22"))
                .andExpect(jsonPath("$.totalDue").value(loan.getTotalDue().intValue()))
                .andExpect(jsonPath("$.status").value(loan.getStatus()));
    }


    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetAllLoans() throws Exception {
        // Arrange
        LoanResponseDto loanResponseDto = new LoanResponseDto(
                1L,
                loan.getStudent().getName(),
                loan.getBookCode().getCode(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getTotalDue()
        );

        when(loanService.getAllLoans()).thenReturn(Collections.singletonList(loanResponseDto));

        // Act & Assert
        mockMvc.perform(get("/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(loanResponseDto.getId()))
                .andExpect(jsonPath("$[0].carnet").value(loanResponseDto.getCarnet()))
                .andExpect(jsonPath("$[0].bookCode").value(loanResponseDto.getBookCode()))
                .andExpect(jsonPath("$[0].loanDate[0]").value(2024))
                .andExpect(jsonPath("$[0].loanDate[1]").value(5))
//                .andExpect(jsonPath("$[0].loanDate[2]").value(19))
                .andExpect(jsonPath("$[0].returnDate[0]").value(2024))
                .andExpect(jsonPath("$[0].returnDate[1]").value(5))
//                .andExpect(jsonPath("$[0].returnDate[2]").value(22))
                .andExpect(jsonPath("$[0].totalDue").value(loanResponseDto.getTotalDue().intValue()))
                .andExpect(jsonPath("$[0].status").value(loanResponseDto.getStatus()));
    }


    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testCloseLoan() throws Exception {
        // Arrange
        Loan closedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .bookCode(book)
                .student(student)
                .totalDue(BigDecimal.valueOf(100))
                .status("returned")
                .build();

        when(loanService.closeLoan(1L)).thenReturn(closedLoan);

        // Act & Assert
        mockMvc.perform(delete("/loans/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(closedLoan.getId()))
                .andExpect(jsonPath("$.loanDate").value(closedLoan.getLoanDate().toString()))
//                .andExpect(jsonPath("$.bookCode.code").value(closedLoan.getBookCode().getCode()))
//                .andExpect(jsonPath("$.student.carnet").value(closedLoan.getStudent().getCarnet()))
                .andExpect(jsonPath("$.totalDue").value(closedLoan.getTotalDue().intValue()))
                .andExpect(jsonPath("$.status").value(closedLoan.getStatus()));
    }
}
