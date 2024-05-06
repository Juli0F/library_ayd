package com.ayd.library.controller;

import com.ayd.library.dto.StudentDto;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.Student;
import com.ayd.library.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    StudentService studentService;

    public StudentController(StudentService service){
        this.studentService = service;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody StudentDto student) throws ServiceException {
        return ResponseEntity.ok(studentService.createStudent(student));
    }
    @GetMapping("/test")
    public String test() {
        return "The application is running!";
    }


    @GetMapping
    public ResponseEntity getAllStudents() {
        return ResponseEntity.ok(studentService.getAllActiveStudents());
    }

    @GetMapping("/{carnet}")
    public ResponseEntity<Student> getStudentByCarnet(@PathVariable String carnet) throws NotFoundException {
        return ResponseEntity.ok(studentService.getStudentByCarnet(carnet));
    }

    @PutMapping("/{carnet}")
    public ResponseEntity<Student> updateStudent(@PathVariable String carnet, @RequestBody Student student) throws NotFoundException {
        return ResponseEntity.ok(studentService.updateStudent(carnet, student));
    }

    @DeleteMapping("/{carnet}")
    public ResponseEntity deleteStudent(@PathVariable String carnet) throws NotFoundException {
            return ResponseEntity.ok(studentService.deleteStudent(carnet));
    }
}
