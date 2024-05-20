package com.ayd.library.unit.service;

import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.enums.ReservationStatusEnum;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.EnoughException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.RequiredEntityException;
import com.ayd.library.model.Book;
import com.ayd.library.model.Reservation;
import com.ayd.library.model.Student;
import com.ayd.library.repository.ReservationRepository;
import com.ayd.library.service.BookService;
import com.ayd.library.service.ReservationService;
import com.ayd.library.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private Student student;
    private Book book;
    private Reservation reservation;

    @BeforeEach
    public void setup() {
        reservationRequestDto = new ReservationRequestDto();
        reservationRequestDto.setReservationDate(LocalDate.now());
        reservationRequestDto.setBookCode("B001");
        reservationRequestDto.setCarnet("ST001");
        reservationRequestDto.setStatus(ReservationStatusEnum.ACTIVE.name().toUpperCase());

        student = new Student();
        student.setCarnet("ST001");
        student.setName("Test Student");

        book = new Book();
        book.setCode("B001");
        book.setTitle("Test Book");
        book.setAvailableCopies(5);

        reservation = Reservation.builder()
                .reservationDate(LocalDate.now())
                .bookCode(book)
                .student(student)
                .status(ReservationStatusEnum.ACTIVE.name())
                .build();
    }

    @Test
    public void testCreateReservation() throws DuplicatedEntityException, NotFoundException, RequiredEntityException, EnoughException {
        when(studentService.getStudentByCarnet(anyString())).thenReturn(student);
        when(bookService.getBookByCode(anyString())).thenReturn(book);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation createdReservation = reservationService.createReservation(reservationRequestDto);

        assertNotNull(createdReservation);
        assertEquals(reservationRequestDto.getReservationDate(), createdReservation.getReservationDate());
        assertEquals(ReservationStatusEnum.ACTIVE.name(), createdReservation.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_BookCodeIsNull() {
        reservationRequestDto.setBookCode(null);

        RequiredEntityException exception = assertThrows(RequiredEntityException.class, () ->
                reservationService.createReservation(reservationRequestDto));

        assertEquals("Book code must not be null", exception.getMessage());
    }

    @Test
    public void testCreateReservation_NotEnoughCopies() throws NotFoundException {
        book.setAvailableCopies(0);
        when(bookService.getBookByCode(anyString())).thenReturn(book);

        EnoughException exception = assertThrows(EnoughException.class, () ->
                reservationService.createReservation(reservationRequestDto));

        assertEquals("Not enough available copies", exception.getMessage());
    }

    @Test
    public void testUpdateReservation() throws NotFoundException {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation updatedReservation = reservationService.updateReservation(1L, reservation);

        assertNotNull(updatedReservation);
        assertEquals(reservation.getReservationDate(), updatedReservation.getReservationDate());
        assertEquals(ReservationStatusEnum.ACTIVE.name(), updatedReservation.getStatus());
    }

    @Test
    public void testUpdateReservation_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reservationService.updateReservation(1L, reservation));

        assertEquals("Reservation not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testGetReservationById() throws NotFoundException {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        Reservation foundReservation = reservationService.getReservationById(1L);

        assertNotNull(foundReservation);
        assertEquals(reservation.getReservationDate(), foundReservation.getReservationDate());
    }

    @Test
    public void testGetReservationById_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reservationService.getReservationById(1L));

        assertEquals("Reservation not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testGetAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getAllReservations();

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
    }

    @Test
    public void testUpdateReservationStatus() throws NotFoundException {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation updatedReservation = reservationService.updateReservationStatus(1L, "INACTIVE");

        assertNotNull(updatedReservation);
        assertEquals("INACTIVE", updatedReservation.getStatus());
    }

    @Test
    public void testUpdateReservationStatus_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reservationService.updateReservationStatus(1L, "INACTIVE"));

        assertEquals("Reservation not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testFindReservationsByStatus() {
        when(reservationRepository.findAllByStatus(anyString())).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.findReservationsByStatus("ACTIVE");

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
    }

    @Test
    public void testGetAllReservation() {
        when(reservationRepository.findReservationDetails()).thenReturn(List.of(reservationRequestDto));

        List<ReservationRequestDto> reservations = reservationService.getAllReservation();

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
    }
}
