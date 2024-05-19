package com.ayd.library.controller;

import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.Career;
import com.ayd.library.model.Student;
import com.ayd.library.service.CareerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/career")
@PreAuthorize("hasAuthority('LIBRARIAN')" )
public class CareerController {

    CareerService careerService;

    public CareerController(CareerService careerService){
        this.careerService = careerService;
    }

    @PostMapping
    public ResponseEntity<Career> create(@RequestBody Career career) throws ServiceException {
        return ResponseEntity.ok(careerService.createCareer(career));
    }
    @GetMapping("all")
    @PreAuthorize("hasAuthority('LIBRARIAN') or hasAuthority('STUDENT')")
    public  ResponseEntity getAll(){
        return  ResponseEntity.ok(careerService.getAllActiveCareers());
    }
    @PutMapping("update/{code}")
    public ResponseEntity<Career> updateEntity(@PathVariable String code, @RequestBody Career career) throws NotFoundException {
        return ResponseEntity.ok(careerService.updateCareer(code, career));
    }

    @PutMapping("soft-delete/{code}")
    public ResponseEntity<Career> softDelete(@PathVariable String code) throws NotFoundException {
        return ResponseEntity.ok(careerService.softDeleteCareer(code));
    }
}
