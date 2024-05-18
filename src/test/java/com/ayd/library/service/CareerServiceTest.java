package com.ayd.library.service;

import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.repository.CareerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {

    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private CareerService careerService;

    private Career career;

    @BeforeEach
    public void setUp() {
        career = Career.builder()
                .code("CS101")
                .name("Computer Science")
                .status(true)
                .build();
    }

    @Test
    public void testCreateCareer() throws DuplicatedEntityException {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.empty());
        when(careerRepository.save(any(Career.class))).thenReturn(career);

        // Act
        Career createdCareer = careerService.createCareer(career);

        // Assert
        assertNotNull(createdCareer);
        assertEquals(career.getCode(), createdCareer.getCode());
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(1)).save(any(Career.class));
    }

    @Test
    public void testCreateCareer_DuplicatedEntityException() {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.of(career));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> careerService.createCareer(career));
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(0)).save(any(Career.class));
    }

    @Test
    public void testUpdateCareer() throws NotFoundException {
        // Arrange
        Career updatedCareer = Career.builder().code("CS101").name("Software Engineering").build();
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.of(career));
        when(careerRepository.save(any(Career.class))).thenReturn(updatedCareer);

        // Act
        Career result = careerService.updateCareer(career.getCode(), updatedCareer);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCareer.getName(), result.getName());
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(1)).save(any(Career.class));
    }

    @Test
    public void UpdateCareer_NotFoundExceptionTest() {
        // Arrange
        Career updatedCareer = Career.builder().code("CS101").name("Software Engineering").build();
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> careerService.updateCareer(career.getCode(), updatedCareer));
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(0)).save(any(Career.class));
    }

    @Test
    public void getCareerByCodeTest() throws NotFoundException {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.of(career));

        // Act
        Career foundCareer = careerService.getCareerByCode(career.getCode());

        // Assert
        assertNotNull(foundCareer);
        assertEquals(career.getCode(), foundCareer.getCode());
        verify(careerRepository, times(1)).findById(career.getCode());
    }

    @Test
    public void getCareerByCodeTest_NotFoundException() {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> careerService.getCareerByCode(career.getCode()));
        verify(careerRepository, times(1)).findById(career.getCode());
    }

    @Test
    public void getAllActiveCareersTest() {
        // Arrange
        List<Career> careers = List.of(career);
        when(careerRepository.findAllByStatus(true)).thenReturn(careers);

        // Act
        List<Career> result = careerService.getAllActiveCareers();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(careerRepository, times(1)).findAllByStatus(true);
    }

    @Test
    public void softDeleteCareerTest() throws NotFoundException {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.of(career));
        when(careerRepository.save(any(Career.class))).thenReturn(career);

        // Act
        Career deletedCareer = careerService.softDeleteCareer(career.getCode());

        // Assert
        assertNotNull(deletedCareer);
        assertFalse(deletedCareer.getStatus());
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(1)).save(any(Career.class));
    }

    @Test
    public void softDeleteCareerTest_NotFoundException() {
        // Arrange
        when(careerRepository.findById(career.getCode())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> careerService.softDeleteCareer(career.getCode()));
        verify(careerRepository, times(1)).findById(career.getCode());
        verify(careerRepository, times(0)).save(any(Career.class));
    }
}
