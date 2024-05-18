package com.ayd.library.dto;

import com.ayd.library.model.Student;

import java.time.LocalDate;

public class StudentDto {
    String carnet;
    String name;

    LocalDate birthDate;
    String career;
    boolean status;

    public StudentDto() {}
    public StudentDto(String carnet, String name, LocalDate birthDate, String career, boolean status) {
        this.carnet = carnet;
        this.name = name;
        this.birthDate = birthDate;
        this.career = career;
        this.status = status;
    }
    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
