package com.ayd.library.repository;

import com.ayd.library.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, String> {
    List<Career> findAllByStatus(boolean status);
}