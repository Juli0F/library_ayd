package com.ayd.library.service;

import com.ayd.library.dto.StudentDto;
import com.ayd.library.dto.StudentLoanResponseDto;
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

    final StudentRepository repository;

    final CareerService careerService;

    @Transactional
    public Student createStudent(StudentDto studentDto) throws DuplicatedEntityException, NotFoundException {
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

        return repository.save(studentEntity);
        //return studentDto;
    }

    @Transactional
    public StudentDto updateStudent(String carnet, StudentDto updatedStudentData) throws NotFoundException {

        Student student = repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));

        student.setName(updatedStudentData.getName());
        student.setBirthDate(updatedStudentData.getBirthDate());

        Career career = careerService.getCareerByCode(updatedStudentData.getCareer());
        student.setCareerCode(career);

        repository.save(student);

        return convertToDto(student);
    }


    public List<StudentDto> getAllActiveStudents() {
        List<Student> students = repository.findAllByStatus(true);
        return students.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public List<StudentLoanResponseDto> getAllActiveStudentsEntity() {
        List <Student> student = repository.findAllByStatus(true);
        return student.stream()
                .map(StudentLoanResponseDto::convertToStudentLoanDto)
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
        assert student.getCareerCode() != null;
        return new StudentDto(
                student.getCarnet(),
                student.getName(),
                student.getBirthDate(),
                student.getCareerCode().getName(),
                student.getStatus()
        );
    }
}
