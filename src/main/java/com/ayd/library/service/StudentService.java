package com.ayd.library.service;

import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Student;
import com.ayd.library.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    StudentRepository repository;
    @Transactional
    public Student createStudent(Student student) throws DuplicatedEntityException {
        if(repository.findById(student.getCarnet()).isPresent()) {
            throw new DuplicatedEntityException("Existe la entidad con carnet: " + student.getCarnet());
        }
        student.setStatus(true);
        return repository.save(student);
    }

    @Transactional
    public Student updateStudent(String carne, Student updatedStudentData) throws NotFoundException {
        return repository.findById(carne).map(existingStudent -> {
            existingStudent.setName(updatedStudentData.getName());
            existingStudent.setCareerCode(updatedStudentData.getCareerCode());
            return repository.save(existingStudent);

        }).orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carne));
    }

    public List<Student> getAllActiveStudents() {
        return repository.findAllByStatus(true);
    }


    public Student getStudentByCarnet(String carnet) throws NotFoundException {
        return repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));
    }
    @Transactional
    public void deleteStudent(String carnet) throws NotFoundException {
        Student student = repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));

        repository.delete(student);
    }

    @Transactional
    public void softDeleteStudent(String carnet) throws NotFoundException {
        Student student = repository.findById(carnet)
                .orElseThrow(() -> new NotFoundException("No se encontró la entidad con carnet: " + carnet));

        student.setStatus(false);
        repository.save(student);
    }




}
