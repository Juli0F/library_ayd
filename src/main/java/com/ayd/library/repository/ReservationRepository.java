package com.ayd.library.repository;

import com.ayd.library.dto.LoanResponseDto;
import com.ayd.library.dto.ReservationRequestDto;
import com.ayd.library.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByStatus(String status);
    @Query("SELECT new com.ayd.library.dto.ReservationRequestDto(s.carnet,b.code,r.reservationDate,r.status,r.id) " +
            "FROM Reservation r JOIN r.student s JOIN r.bookCode b")
    List<ReservationRequestDto> findReservationDetails();
}