package com.ayd.library.service;

import com.ayd.library.dto.StudentDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.model.Student;
import com.ayd.library.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    StudentRepository repository;

    @Autowired
    CareerService careerService;
    @Transactional
    public StudentDto createStudent(StudentDto studentDto) throws DuplicatedEntityException, NotFoundException {
        if(repository.findById(studentDto.getCarnet()).isPresent()) {
            throw new DuplicatedEntityException("Existe la entidad con carnet: " + studentDto.getCarnet());
        }

        Career career = careerService.getCareerByCode(studentDto.getCareer());

        Student studentEntity = Student.builder()
                .carnet(studentDto.getCarnet())
                .name(studentDto.getName())
                .status(true)
                .birthDate(studentDto.getBirthDate())
                .careerCode(career)
                .build();

        repository.save(studentEntity);
        return studentDto;
    }

    @Transactional
    public Student updateStudent(String carne, Student updatedStudentData) throws NotFoundException {
        return repository.findById(carne).map(existingStudent -> {
            existingStudent.setName(updatedStudentData.getName());
            existingStudent.setCareerCode(updatedStudentData.getCareerCode());
            return repository.save(existingStudent);

        }).orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carne));
    }

    public List<StudentDto> getAllActiveStudents() {
        List<Student> students = repository.findAllByStatus(true);
        return students.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public Student getStudentByCarnet(String carnet) throws NotFoundException {
        return repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));
    }
    @Transactional
    public String deleteStudent(String carnet) throws NotFoundException {
        Student student = repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));

        repository.delete(student);
        return carnet;
    }

    @Transactional
    public Student softDeleteStudent(String carnet) throws NotFoundException {
        Student student = repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));

        student.setStatus(false);
        repository.save(student);
        return student;
    }


    public StudentDto convertToDto(Student student) {
        if (student.getCareerCode() != null && !Hibernate.isInitialized(student.getCareerCode())) {
            Hibernate.initialize(student.getCareerCode());
        }
        return new StudentDto(
                student.getCarnet(),
                student.getName(),
                student.getBirthDate(),
                student.getCareerCode().getName(),
                student.getStatus()
        );
    }
}
