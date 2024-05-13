package com.ayd.library.dto;

import com.ayd.library.model.Loan;
import com.ayd.library.model.Student;
import org.hibernate.Hibernate;

import java.util.Set;

public class StudentLoanResponseDto {
    String carnet;
    String name;
    boolean status;
    String career;
    Set<Loan> loans;

    public StudentLoanResponseDto(String carnet, String name, String career, boolean status, Set<Loan> loans) {
        this.carnet = carnet;
        this.name = name;
        this.status = status;
        this.career = career;
        this.loans = loans;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Set<Loan> getLoans() {
        return loans;
    }

    public void setLoans(Set<Loan> loans) {
        this.loans = loans;
    }

    public static  StudentLoanResponseDto convertToStudentLoanDto(Student student) {
        if (student.getCareerCode() != null && !Hibernate.isInitialized(student.getCareerCode())) {
            Hibernate.initialize(student.getCareerCode());
        }
        //String carnet, String name, String career, boolean status, Set<Loan> loans
        return new StudentLoanResponseDto(
                student.getCarnet(),
                student.getName(),
                student.getCareerCode().getName(),
                student.getStatus(),
                student.getLoans()
        );
    }
}
