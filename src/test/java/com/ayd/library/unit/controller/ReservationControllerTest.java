package com.ayd.library.unit.controller;

import com.ayd.library.controller.ReservationController;
import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.enums.ReservationStatusEnum;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.model.Reservation;
import com.ayd.library.model.Student;
import com.ayd.library.service.ReservationService;
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

public class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private Reservation reservation;
    private ReservationRequestDto reservationRequestDto;
    private Book book;
    private Student student;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();

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

        reservation = Reservation.builder()
                .id(1L)
                .reservationDate(LocalDate.now())
                .bookCode(book)
                .student(student)
                .status("ACTIVE")
                .build();
///String carnet, String bookCode, LocalDate reservationDate, String status, Long id
        reservationRequestDto = new ReservationRequestDto(
                student.getCarnet(),
                "B001",
                LocalDate.now(),
                ReservationStatusEnum.ACTIVE.name().toUpperCase(),
                1L
        );
    }

 
    @Test
    @WithMockUser(authorities = {"LIBRARIAN", "STUDENT"})
    public void testUpdateReservationStatus() throws Exception {
        // Arrange
        Reservation updatedReservation = Reservation.builder()
                .id(1L)
                .reservationDate(LocalDate.now())
                .bookCode(book)
                .student(student)
                .status("COMPLETED")
                .build();

        when(reservationService.updateReservationStatus(any(Long.class), any(String.class))).thenReturn(updatedReservation);

        // Act & Assert
        mockMvc.perform(patch("/reservations/1/status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedReservation.getId()))
                //.andExpect(jsonPath("$.reservationDate").value(updatedReservation.getReservationDate().toString()))
//                .andExpect(jsonPath("$.bookCode.code").value(updatedReservation.getBookCode().getCode()))
//                .andExpect(jsonPath("$.student.carnet").value(updatedReservation.getStudent().getCarnet()))
                .andExpect(jsonPath("$.status").value(updatedReservation.getStatus()));
    }


}
