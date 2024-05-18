package com.ayd.library.service;

import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.enums.ReservationStatusEnum;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.EnoughException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.RequiredEntityException;
import com.ayd.library.model.Reservation;
import com.ayd.library.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    final ReservationRepository repository;

    final StudentService studentService;

    final BookService bookService;

    @Transactional
    public Reservation createReservation(ReservationRequestDto reservation) throws DuplicatedEntityException, RequiredEntityException, NotFoundException, EnoughException {
        if (reservation.getBookCode() == null) {
            throw new RequiredEntityException("Book code must not be null");
        }

        var studentEntity = studentService.getStudentByCarnet(reservation.getCarnet());
        var bookEntity = bookService.getBookByCode(reservation.getBookCode());

        if (bookEntity.getAvailableCopies() == 0) {
            throw new EnoughException("Not enough available copies");
        }

        if (repository.findById(reservation.getId()).isPresent()) {
            throw new DuplicatedEntityException("Reservation with ID already exists: " + reservation.getId());
        }

        Reservation entity = Reservation.builder()
                .reservationDate(reservation.getReservationDate())
                .bookCode(bookEntity)
                .status(ReservationStatusEnum.ACTIVE.name())
                .student(studentEntity)
                .build();

        return repository.save(entity);
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation updatedReservation) throws NotFoundException {
        return repository.findById(id)
                .map(existingReservation -> {
                    existingReservation.setBookCode(updatedReservation.getBookCode());
                    existingReservation.setStudent(updatedReservation.getStudent());
                    existingReservation.setReservationDate(updatedReservation.getReservationDate());
                    existingReservation.setStatus(updatedReservation.getStatus());
                    return repository.save(existingReservation);
                })
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
    }

    public Reservation getReservationById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
    }

    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }


    @Transactional
    public Reservation updateReservationStatus(Long id, String status) throws NotFoundException {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        return repository.save(reservation);
    }

    public List<Reservation> findReservationsByStatus(String status) {
        return repository.findAllByStatus(status);
    }

    public List<ReservationRequestDto> getAllReservation() {
        return repository.findReservationDetails();
    }

}
