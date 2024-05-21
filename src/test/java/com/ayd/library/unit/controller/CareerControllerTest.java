package com.ayd.library.unit.controller;

import com.ayd.library.controller.CareerController;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.Career;
import com.ayd.library.service.CareerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CareerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CareerService careerService;

    @InjectMocks
    private CareerController careerController;

    private Career career;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(careerController).build();

        career = Career.builder()
                .code("C001")
                .name("Computer Science")
                .status(true)
                .build();
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testCreateCareer() throws Exception {
        // Arrange
        when(careerService.createCareer(any(Career.class))).thenReturn(career);

        String content = """
                {
                    "code": "%s",
                    "name": "%s",
                    "status": %b
                }
                """.formatted(career.getCode(), career.getName(), career.getStatus());

        // Act & Assert
        mockMvc.perform(post("/career")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(career.getCode()))
                .andExpect(jsonPath("$.name").value(career.getName()))
                .andExpect(jsonPath("$.status").value(career.getStatus()));
    }

    @Test
    @WithMockUser(authorities = {"LIBRARIAN", "STUDENT"})
    public void testGetAllActiveCareers() throws Exception {
        // Arrange
        when(careerService.getAllActiveCareers()).thenReturn(Collections.singletonList(career));

        // Act & Assert
        mockMvc.perform(get("/career/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value(career.getCode()))
                .andExpect(jsonPath("$[0].name").value(career.getName()))
                .andExpect(jsonPath("$[0].status").value(career.getStatus()));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testUpdateCareer() throws Exception {
        // Arrange
        Career updatedCareer = Career.builder()
                .code("C001")
                .name("Updated Computer Science")
                .status(true)
                .build();

        when(careerService.updateCareer(any(String.class), any(Career.class))).thenReturn(updatedCareer);

        String content = """
                {
                    "code": "%s",
                    "name": "%s",
                    "status": %b
                }
                """.formatted(updatedCareer.getCode(), updatedCareer.getName(), updatedCareer.getStatus());

        // Act & Assert
        mockMvc.perform(put("/career/update/C001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(updatedCareer.getCode()))
                .andExpect(jsonPath("$.name").value(updatedCareer.getName()))
                .andExpect(jsonPath("$.status").value(updatedCareer.getStatus()));
    }

    @Test
    @WithMockUser(authorities = "LIBRARIAN")
    public void testSoftDeleteCareer() throws Exception {
        // Arrange
        Career softDeletedCareer = Career.builder()
                .code("C001")
                .name("Computer Science")
                .status(false)
                .build();

        when(careerService.softDeleteCareer("C001")).thenReturn(softDeletedCareer);

        // Act & Assert
        mockMvc.perform(put("/career/soft-delete/C001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(softDeletedCareer.getCode()))
                .andExpect(jsonPath("$.name").value(softDeletedCareer.getName()))
                .andExpect(jsonPath("$.status").value(softDeletedCareer.getStatus()));
    }
}
