package com.ayd.library.repository;

import com.ayd.library.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findAllByStatus(boolean status);
}