package com.ayd.library.service;

import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Career;
import com.ayd.library.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository repository;

    @Transactional
    public Career createCareer(Career career) throws DuplicatedEntityException {
        if(repository.findById(career.getCode()).isPresent()) {
            throw new DuplicatedEntityException("Existe la carrera con el codigo: " + career.getCode());
        }
        career.setStatus(true);
        return repository.save(career);
    }
    @Transactional
    public Career updateCareer(String code, Career updatedCareer) throws NotFoundException {
        return repository.findById(code)
                .map(existingCareer -> {
                    existingCareer.setName(updatedCareer.getName());
                    return repository.save(existingCareer);
                })
                .orElseThrow(() -> new NotFoundException("No se encontró la carrera con código: " + code));
    }

    public Career getCareerByCode(String code) throws NotFoundException {
        return repository.findById(code)
                .filter(Career::getStatus)
                .orElseThrow(() -> new NotFoundException("No se encontró la carrera con código: " + code));
    }
    public List<Career> getAllActiveCareers() {
        return repository.findAllByStatus(true);
    }
    @Transactional
    public Career softDeleteCareer(String code) throws NotFoundException {
        Career career = repository.findById(code)
                .orElseThrow(() -> new NotFoundException("No se encontró la carrera con código: " + code));

        career.setStatus(false);
        return repository.save(career);

    }





}
