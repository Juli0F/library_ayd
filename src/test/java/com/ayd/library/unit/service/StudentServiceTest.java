package com.ayd.library.unit.service;

import com.ayd.library.dto.StudentDto;
import com.ayd.library.dto.StudentLoanResponseDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.model.Student;
import com.ayd.library.repository.StudentRepository;
import com.ayd.library.service.CareerService;
import com.ayd.library.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CareerService careerService;

    @InjectMocks
    private StudentService studentService;

    private StudentDto studentDto;
    private Student student;
    private Career career;

    @BeforeEach
    public void setUp() {
        studentDto = new StudentDto();
        studentDto.setCarnet("123456");
        studentDto.setName("Julio Test");
        studentDto.setBirthDate(LocalDate.of(2000, 1, 1));
        studentDto.setCareer("CS101");
        studentDto.setStatus(true);

        career = Career.builder()
                .code("CS101")
                .name("Computer Science")
                .status(true)
                .build();

        student = Student.builder()
                .carnet("123456")
                .name("Julio Test")
                .birthDate(LocalDate.of(2000, 1, 1))
                .careerCode(career)
                .status(true)
                .build();
    }

    @Test
    public void testCreateStudent() throws DuplicatedEntityException, NotFoundException {
        // Arrange
        when(studentRepository.findById(studentDto.getCarnet())).thenReturn(Optional.empty());
        when(careerService.getCareerByCode(studentDto.getCareer())).thenReturn(career);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        StudentDto createdStudent = studentService.createStudent(studentDto);

        // Assert
        assertNotNull(createdStudent);
        assertEquals(studentDto.getCarnet(), createdStudent.getCarnet());
        verify(studentRepository, times(1)).findById(studentDto.getCarnet());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testCreateStudent_DuplicatedEntityException() {
        // Arrange
        when(studentRepository.findById(studentDto.getCarnet())).thenReturn(Optional.of(student));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> studentService.createStudent(studentDto));
        verify(studentRepository, times(1)).findById(studentDto.getCarnet());
        verify(studentRepository, times(0)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudent() throws NotFoundException {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.of(student));
        when(careerService.getCareerByCode(studentDto.getCareer())).thenReturn(career);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        StudentDto updatedStudent = studentService.updateStudent(student.getCarnet(), studentDto);

        // Assert
        assertNotNull(updatedStudent);
        assertEquals(studentDto.getName(), updatedStudent.getName());
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudent_NotFoundException() {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.updateStudent(student.getCarnet(), studentDto));
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(0)).save(any(Student.class));
    }

    @Test
    public void testGetAllActiveStudents() {
        // Arrange
        List<Student> students = List.of(student);
        when(studentRepository.findAllByStatus(true)).thenReturn(students);

        // Act
        List<StudentDto> result = studentService.getAllActiveStudents();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAllByStatus(true);
    }

    @Test
    public void testGetAllActiveStudentsEntity() {
        // Arrange
        List<Student> students = List.of(student);
        when(studentRepository.findAllByStatus(true)).thenReturn(students);

        // Act
        List<StudentLoanResponseDto> result = studentService.getAllActiveStudentsEntity();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAllByStatus(true);
    }

    @Test
    public void testGetStudentByCarnet() throws NotFoundException {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.of(student));

        // Act
        Student foundStudent = studentService.getStudentByCarnet(student.getCarnet());

        // Assert
        assertNotNull(foundStudent);
        assertEquals(student.getCarnet(), foundStudent.getCarnet());
        verify(studentRepository, times(1)).findById(student.getCarnet());
    }

    @Test
    public void testGetStudentByCarnet_NotFoundException() {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.getStudentByCarnet(student.getCarnet()));
        verify(studentRepository, times(1)).findById(student.getCarnet());
    }

    @Test
    public void testDeleteStudent() throws NotFoundException {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.of(student));

        // Act
        String deletedCarnet = studentService.deleteStudent(student.getCarnet());

        // Assert
        assertNotNull(deletedCarnet);
        assertEquals(student.getCarnet(), deletedCarnet);
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(1)).delete(any(Student.class));
    }

    @Test
    public void testDeleteStudent_NotFoundException() {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.deleteStudent(student.getCarnet()));
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(0)).delete(any(Student.class));
    }

    @Test
    public void testSoftDeleteStudent() throws NotFoundException {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student softDeletedStudent = studentService.softDeleteStudent(student.getCarnet());

        // Assert
        assertNotNull(softDeletedStudent);
        assertFalse(softDeletedStudent.getStatus());
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testSoftDeleteStudent_NotFoundException() {
        // Arrange
        when(studentRepository.findById(student.getCarnet())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.softDeleteStudent(student.getCarnet()));
        verify(studentRepository, times(1)).findById(student.getCarnet());
        verify(studentRepository, times(0)).save(any(Student.class));
    }
}
