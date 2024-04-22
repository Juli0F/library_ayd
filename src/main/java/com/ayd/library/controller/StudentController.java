package com.ayd.library.controller;

import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.Student;
import com.ayd.library.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    StudentService service;

    public StudentController(StudentService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody Student student) throws ServiceException {
        return ResponseEntity.ok(service.createStudent(student));
    }
    @GetMapping("/test")
    public String test() {
        return "The application is running!";
    }
}
