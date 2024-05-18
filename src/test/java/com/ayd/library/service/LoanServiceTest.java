package com.ayd.library.service;

import com.ayd.library.dto.LoanRequestDto;
import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.enums.LoanStatus;
import com.ayd.library.exception.*;
import com.ayd.library.model.Book;
import com.ayd.library.model.Loan;
import com.ayd.library.model.Student;
import com.ayd.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    private LoanRequestDto loanRequestDto;
    private Loan loan;
    private Student student;
    private Book book;

    @BeforeEach
    public void setUp() {
        loanRequestDto = new LoanRequestDto("123456",
                "BK101",
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                LoanStatus.RETURNED.name(),
                BigDecimal.valueOf(100),
                1L);

        student = Student.builder()
                .carnet("123456")
                .name("Julio Test")
                .build();

        book = Book.builder()
                .code("BK101")
                .title("Test Book")
                .availableCopies(5)
                .build();

        loan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(14))
                .bookCode(book)
                .student(student)
                .totalDue(BigDecimal.valueOf(100))
                .status(LoanStatus.ACTIVE.name())
                .build();
    }

    @Test
    public void testCreateLoan() throws DuplicatedEntityException, NotFoundException, QuantityException, RequiredEntityException, EnoughException {
        // Arrange
        when(loanRepository.findById(loanRequestDto.getId())).thenReturn(Optional.empty());
        when(studentService.getStudentByCarnet(loanRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(loanRequestDto.getBookCode())).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Act
        Loan createdLoan = loanService.createLoan(loanRequestDto);

        // Assert
        assertNotNull(createdLoan);
        assertEquals(loanRequestDto.getId(), createdLoan.getId());
        verify(loanRepository, times(1)).findById(loanRequestDto.getId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testCreateLoan_DuplicatedEntityException() throws NotFoundException {
        // Arrange
        when(loanRepository.findById(loanRequestDto.getId())).thenReturn(Optional.of(loan));
        when(studentService.getStudentByCarnet(loanRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(loanRequestDto.getBookCode())).thenReturn(book);

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> loanService.createLoan(loanRequestDto));
        verify(loanRepository, times(1)).findById(loanRequestDto.getId());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testCreateLoan_QuantityException() {
        // Arrange
        loanRequestDto.setTotalDue(BigDecimal.valueOf(-1));

        // Act & Assert
        assertThrows(QuantityException.class, () -> loanService.createLoan(loanRequestDto));
        verify(loanRepository, times(0)).findById(anyLong());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testCreateLoan_RequiredEntityException() {
        // Arrange
        loanRequestDto.setBookCode(null);

        // Act & Assert
        assertThrows(RequiredEntityException.class, () -> loanService.createLoan(loanRequestDto));
        verify(loanRepository, times(0)).findById(anyLong());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testCreateLoan_EnoughException() throws NotFoundException {
        // Arrange
        book.setAvailableCopies(0);
        when(studentService.getStudentByCarnet(loanRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(loanRequestDto.getBookCode())).thenReturn(book);

        // Act & Assert
        assertThrows(EnoughException.class, () -> loanService.createLoan(loanRequestDto));
        verify(loanRepository, times(0)).findById(anyLong());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoan() throws NotFoundException {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Act
        Loan updatedLoan = loanService.updateLoan(loan.getId(), loan);

        // Assert
        assertNotNull(updatedLoan);
        assertEquals(loan.getId(), updatedLoan.getId());
        verify(loanRepository, times(1)).findById(loan.getId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoan_NotFoundException() {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> loanService.updateLoan(loan.getId(), loan));
        verify(loanRepository, times(1)).findById(loan.getId());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testGetLoanById() throws NotFoundException {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));

        // Act
        Loan foundLoan = loanService.getLoanById(loan.getId());

        // Assert
        assertNotNull(foundLoan);
        assertEquals(loan.getId(), foundLoan.getId());
        verify(loanRepository, times(1)).findById(loan.getId());
    }

    @Test
    public void testGetLoanById_NotFoundException() {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> loanService.getLoanById(loan.getId()));
        verify(loanRepository, times(1)).findById(loan.getId());
    }

    @Test
    public void testGetAllLoans() {
        // Arrange
        LoanResponseDto loanResponseDto = new LoanResponseDto(1L,"123456",
                "BK101",
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                LoanStatus.RETURNED.name(),
                BigDecimal.valueOf(100));
        List<LoanResponseDto> loans = List.of(loanResponseDto);
        when(loanRepository.findLoanDetails()).thenReturn(loans);

        // Act
        List<LoanResponseDto> result = loanService.getAllLoans();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findLoanDetails();
    }

    @Test
    public void testCloseLoan() throws NotFoundException {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Act
        Loan closedLoan = loanService.closeLoan(loan.getId());

        // Assert
        assertNotNull(closedLoan);
        assertEquals("returned", closedLoan.getStatus());
        verify(loanRepository, times(1)).findById(loan.getId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testCloseLoan_NotFoundException() {
        // Arrange
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> loanService.closeLoan(loan.getId()));
        verify(loanRepository, times(1)).findById(loan.getId());
        verify(loanRepository, times(0)).save(any(Loan.class));
    }

    @Test
    public void testFindLoansByStatus() {
        // Arrange
        List<Loan> loans = List.of(loan);
        when(loanRepository.findAllByStatus(LoanStatus.ACTIVE.name())).thenReturn(loans);

        // Act
        List<Loan> result = loanService.findLoansByStatus(LoanStatus.ACTIVE.name());

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findAllByStatus(LoanStatus.ACTIVE.name());
    }
}
