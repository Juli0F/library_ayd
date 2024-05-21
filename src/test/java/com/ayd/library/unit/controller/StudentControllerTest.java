package com.ayd.library.unit.controller;

import com.ayd.library.controller.StudentController;
import com.ayd.library.dto.StudentDto;
import com.ayd.library.dto.StudentLoanResponseDto;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.Student;
import com.ayd.library.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private Student student;
    private StudentDto studentDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();

        studentDto = new StudentDto("S001", "Julio Test", LocalDate.of(1995, 1, 1), "Computer Science", true);

        student = Student.builder()
                .carnet("S001")
                .name("Julio Test")
                .birthDate(LocalDate.of(1995, 1, 1))
                .status(true)
                .build();
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testCreateStudent() throws Exception {
        // Arrange
        when(studentService.createStudent(any(StudentDto.class))).thenReturn(student);

        String content = """
                {
                    "carnet": "%s",
                    "name": "%s",
                    "birthDate": "%s",
                    "career": "%s",
                    "status": %b
                }
                """.formatted(studentDto.getCarnet(), studentDto.getName(), studentDto.getBirthDate(), studentDto.getCareer(), studentDto.isStatus());

        // Act & Assert
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carnet").value(studentDto.getCarnet()))
                .andExpect(jsonPath("$.name").value(studentDto.getName()))
//                .andExpect(jsonPath("$.birthDate").value(studentDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.status").value(studentDto.isStatus()));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetAllStudents() throws Exception {
        // Arrange
        when(studentService.getAllActiveStudents()).thenReturn(Collections.singletonList(studentDto));

        // Act & Assert
        mockMvc.perform(get("/student")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carnet").value(studentDto.getCarnet()))
                .andExpect(jsonPath("$[0].name").value(studentDto.getName()))
                //.andExpect(jsonPath("$[0].birthDate").value(studentDto.getBirthDate().toString()))
                .andExpect(jsonPath("$[0].career").value(studentDto.getCareer()))
                .andExpect(jsonPath("$[0].status").value(studentDto.isStatus()));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testGetStudentByCarnet() throws Exception {
        // Arrange
        when(studentService.getStudentByCarnet("S001")).thenReturn(student);

        // Act & Assert
        mockMvc.perform(get("/student/S001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carnet").value(student.getCarnet()))
                .andExpect(jsonPath("$.name").value(student.getName()))
//                .andExpect(jsonPath("$.birthDate").value(student.getBirthDate().toString()))
                .andExpect(jsonPath("$.status").value(student.getStatus()));
    }

    @Test
    @WithMockUser(authorities = {"LIBRARIAN", "STUDENT"})
    public void testUpdateStudent() throws Exception {
        // Arrange
        StudentDto updatedStudentDto = new StudentDto("S001", "Julio Test Updated", LocalDate.of(1995, 1, 1), "Mathematics", true);

        when(studentService.updateStudent(any(String.class), any(StudentDto.class))).thenReturn(updatedStudentDto);

        String content = """
                {
                    "carnet": "%s",
                    "name": "%s",
                    "birthDate": "%s",
                    "career": "%s",
                    "status": %b
                }
                """.formatted(updatedStudentDto.getCarnet(), updatedStudentDto.getName(), updatedStudentDto.getBirthDate(), updatedStudentDto.getCareer(), updatedStudentDto.isStatus());

        // Act & Assert
        mockMvc.perform(put("/student/S001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carnet").value(updatedStudentDto.getCarnet()))
                .andExpect(jsonPath("$.name").value(updatedStudentDto.getName()))
//                .andExpect(jsonPath("$.birthDate").value(updatedStudentDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.career").value(updatedStudentDto.getCareer()))
                .andExpect(jsonPath("$.status").value(updatedStudentDto.isStatus()));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testDeleteStudent() throws Exception {
        // Arrange
        when(studentService.deleteStudent("S001")).thenReturn("S001");

        // Act & Assert
        mockMvc.perform(delete("/student/S001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("S001"));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testTestEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/student/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("The application is running!"));
    }
}
