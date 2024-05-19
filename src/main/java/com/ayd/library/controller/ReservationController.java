package com.ayd.library.controller;


import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.exception.*;
import com.ayd.library.model.Reservation;
import com.ayd.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequestDto reservation) throws ServiceException {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok( reservationService.getReservationById(id) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) throws NotFoundException {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(@PathVariable Long id, @RequestParam String status) throws NotFoundException {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }
    @GetMapping
    public ResponseEntity<List<ReservationRequestDto>> getAllLoans() {
        return ResponseEntity.ok(reservationService.getAllReservation());
    }
}
