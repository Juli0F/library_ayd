package com.ayd.library.service;

import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Reservation;
import com.ayd.library.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    @Transactional
    public Reservation createReservation(Reservation reservation) throws DuplicatedEntityException {
        if (repository.findById(reservation.getId()).isPresent()) {
            throw new DuplicatedEntityException("Reservation with ID already exists: " + reservation.getId());
        }
        reservation.setStatus("active");
        return repository.save(reservation);
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
}
