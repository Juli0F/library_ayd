package com.ayd.library.service;

import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.enums.ReservationStatusEnum;
import com.ayd.library.exception.*;
import com.ayd.library.model.Book;
import com.ayd.library.model.Reservation;
import com.ayd.library.model.Student;
import com.ayd.library.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationRequestDto reservationRequestDto;
    private Reservation reservation;
    private Student student;
    private Book book;

    @BeforeEach
    public void setUp() {
        reservationRequestDto = new  ReservationRequestDto("123456",
                "BK101",
                LocalDate.now(),
                ReservationStatusEnum.COMPLETED.name(),
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

        reservation = Reservation.builder()
                .id(1L)
                .reservationDate(LocalDate.now())
                .bookCode(book)
                .student(student)
                .status(ReservationStatusEnum.ACTIVE.name())
                .build();
    }

    @Test
    public void testCreateReservation() throws DuplicatedEntityException, RequiredEntityException, NotFoundException, EnoughException {
        // Arrange
        when(reservationRepository.findById(reservationRequestDto.getId())).thenReturn(Optional.empty());
        when(studentService.getStudentByCarnet(reservationRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(reservationRequestDto.getBookCode())).thenReturn(book);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);

        // Assert
        assertNotNull(createdReservation);
        assertEquals(reservationRequestDto.getId(), createdReservation.getId());
        verify(reservationRepository, times(1)).findById(reservationRequestDto.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_DuplicatedEntityException() throws NotFoundException {
        // Arrange
        when(reservationRepository.findById(reservationRequestDto.getId())).thenReturn(Optional.of(reservation));
        when(studentService.getStudentByCarnet(reservationRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(reservationRequestDto.getBookCode())).thenReturn(book);

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> reservationService.createReservation(reservationRequestDto));
        verify(reservationRepository, times(1)).findById(reservationRequestDto.getId());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }


    @Test
    public void testCreateReservation_RequiredEntityException() {
        // Arrange
        reservationRequestDto.setBookCode(null);

        // Act & Assert
        assertThrows(RequiredEntityException.class, () -> reservationService.createReservation(reservationRequestDto));
        verify(reservationRepository, times(0)).findById(anyLong());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_EnoughException() throws NotFoundException {
        // Arrange
        book.setAvailableCopies(0);
        when(studentService.getStudentByCarnet(reservationRequestDto.getCarnet())).thenReturn(student);
        when(bookService.getBookByCode(reservationRequestDto.getBookCode())).thenReturn(book);

        // Act & Assert
        assertThrows(EnoughException.class, () -> reservationService.createReservation(reservationRequestDto));

        // Verificación: asegurarse de que no se hizo ninguna llamada al método findById ni save del repositorio
        verify(reservationRepository, never()).findById(anyLong());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }


    @Test
    public void testUpdateReservation() throws NotFoundException {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Reservation updatedReservation = reservationService.updateReservation(reservation.getId(), reservation);

        // Assert
        assertNotNull(updatedReservation);
        assertEquals(reservation.getId(), updatedReservation.getId());
        verify(reservationRepository, times(1)).findById(reservation.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testUpdateReservation_NotFoundException() {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> reservationService.updateReservation(reservation.getId(), reservation));
        verify(reservationRepository, times(1)).findById(reservation.getId());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    public void testGetReservationById() throws NotFoundException {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // Act
        Reservation foundReservation = reservationService.getReservationById(reservation.getId());

        // Assert
        assertNotNull(foundReservation);
        assertEquals(reservation.getId(), foundReservation.getId());
        verify(reservationRepository, times(1)).findById(reservation.getId());
    }

    @Test
    public void testGetReservationById_NotFoundException() {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> reservationService.getReservationById(reservation.getId()));
        verify(reservationRepository, times(1)).findById(reservation.getId());
    }

    @Test
    public void testGetAllReservations() {
        // Arrange
        List<Reservation> reservations = List.of(reservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Act
        List<Reservation> result = reservationService.getAllReservations();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateReservationStatus() throws NotFoundException {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Reservation updatedReservation = reservationService.updateReservationStatus(reservation.getId(), "CLOSED");

        // Assert
        assertNotNull(updatedReservation);
        assertEquals("CLOSED", updatedReservation.getStatus());
        verify(reservationRepository, times(1)).findById(reservation.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testUpdateReservationStatus_NotFoundException() {
        // Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> reservationService.updateReservationStatus(reservation.getId(), "CLOSED"));
        verify(reservationRepository, times(1)).findById(reservation.getId());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    public void testFindReservationsByStatus() {
        // Arrange
        List<Reservation> reservations = List.of(reservation);
        when(reservationRepository.findAllByStatus(ReservationStatusEnum.ACTIVE.name())).thenReturn(reservations);

        // Act
        List<Reservation> result = reservationService.findReservationsByStatus(ReservationStatusEnum.ACTIVE.name());

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAllByStatus(ReservationStatusEnum.ACTIVE.name());
    }
}
