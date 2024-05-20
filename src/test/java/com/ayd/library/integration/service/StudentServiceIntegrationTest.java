package com.ayd.library.integration.service;

import com.ayd.library.dto.StudentDto;
import com.ayd.library.dto.StudentLoanResponseDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.model.Student;
import com.ayd.library.repository.CareerRepository;
import com.ayd.library.repository.StudentRepository;
import com.ayd.library.service.CareerService;
import com.ayd.library.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class StudentServiceIntegrationTest {

    @Container
    public static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CareerService careerService;

    private StudentDto studentDto;
    private Career career;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        career = new Career();
        career.setCode("C001");
        career.setName("Computer Science");
        career.setStatus(true);
        careerRepository.save(career);

        studentDto = new StudentDto();
        studentDto.setCarnet("ST001");
        studentDto.setName("Julio Test");
        studentDto.setBirthDate(LocalDate.of(1995, 1, 1));
        studentDto.setCareer("C001");
        studentDto.setStatus(true);
    }

    @Test
    public void testCreateStudent() throws DuplicatedEntityException, NotFoundException {
        // Act
        var createdStudent = studentService.createStudent(studentDto);

        // Assert
        assertNotNull(createdStudent);
        assertEquals(studentDto.getCarnet(), createdStudent.getCarnet());
        assertEquals(studentDto.getName(), createdStudent.getName());
    }

    @Test
    public void testCreateStudent_DuplicatedEntityException() throws DuplicatedEntityException, NotFoundException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act & Assert
        DuplicatedEntityException thrown = assertThrows(DuplicatedEntityException.class, () -> {
            studentService.createStudent(studentDto);
        });

        assertEquals("Existe la entidad con carnet: ST001", thrown.getMessage());
    }

    @Test
    public void testUpdateStudent() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        studentService.createStudent(studentDto);
        StudentDto updatedStudentDto = new StudentDto();
        updatedStudentDto.setName("Julio Test");
        updatedStudentDto.setBirthDate(LocalDate.of(1996, 2, 2));
        updatedStudentDto.setCareer("C001");

        // Act
        StudentDto updatedStudent = studentService.updateStudent(studentDto.getCarnet(), updatedStudentDto);

        // Assert
        assertNotNull(updatedStudent);
        assertEquals(updatedStudentDto.getName(), updatedStudent.getName());
        assertEquals(updatedStudentDto.getBirthDate(), updatedStudent.getBirthDate());
    }

    @Test
    public void testUpdateStudent_NotFoundException() {
        // Arrange
        StudentDto updatedStudentDto = new StudentDto();
        updatedStudentDto.setName("Julio Test");
        updatedStudentDto.setBirthDate(LocalDate.of(1996, 2, 2));
        updatedStudentDto.setCareer("C001");

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.updateStudent("NonExistentCarnet", updatedStudentDto);
        });

        assertEquals("No se encontró la entidad con carnet: NonExistentCarnet", thrown.getMessage());
    }

    @Test
    public void testGetAllActiveStudents() throws DuplicatedEntityException, NotFoundException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act
        List<StudentDto> activeStudents = studentService.getAllActiveStudents();

        // Assert
        assertNotNull(activeStudents);
        assertFalse(activeStudents.isEmpty());
        assertEquals(1, activeStudents.size());
    }

    @Test
    public void testGetAllActiveStudentsEntity() throws DuplicatedEntityException, NotFoundException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act
        List<StudentLoanResponseDto> activeStudents = studentService.getAllActiveStudentsEntity();

        // Assert
        assertNotNull(activeStudents);
        assertFalse(activeStudents.isEmpty());
        assertEquals(1, activeStudents.size());
    }

    @Test
    public void testGetStudentByCarnet() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act
        Student foundStudent = studentService.getStudentByCarnet(studentDto.getCarnet());

        // Assert
        assertNotNull(foundStudent);
        assertEquals(studentDto.getCarnet(), foundStudent.getCarnet());
    }

    @Test
    public void testGetStudentByCarnet_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.getStudentByCarnet("NonExistentCarnet");
        });

        assertEquals("No se encontró la entidad con carnet: NonExistentCarnet", thrown.getMessage());
    }

    @Test
    public void testDeleteStudent() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act
        String deletedCarnet = studentService.deleteStudent(studentDto.getCarnet());

        // Assert
        assertEquals(studentDto.getCarnet(), deletedCarnet);
        assertThrows(NotFoundException.class, () -> {
            studentService.getStudentByCarnet(studentDto.getCarnet());
        });
    }

    @Test
    public void testDeleteStudent_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.deleteStudent("NonExistentCarnet");
        });

        assertEquals("No se encontró la entidad con carnet: NonExistentCarnet", thrown.getMessage());
    }

    @Test
    public void testSoftDeleteStudent() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        studentService.createStudent(studentDto);

        // Act
        Student softDeletedStudent = studentService.softDeleteStudent(studentDto.getCarnet());

        // Assert
        assertNotNull(softDeletedStudent);
        assertFalse(softDeletedStudent.getStatus());
    }

    @Test
    public void testSoftDeleteStudent_NotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            studentService.softDeleteStudent("NonExistentCarnet");
        });

        assertEquals("No se encontró la entidad con carnet: NonExistentCarnet", thrown.getMessage());
    }
}
