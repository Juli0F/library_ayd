CREATE DATABASE library_ayd;
USE library_ayd;

CREATE TABLE Career (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status TINYINT(1)
);

CREATE TABLE Student (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthDate DATE NOT NULL,
    careerCode VARCHAR(10) NOT NULL,
    status TINYINT(1) DEFAULT 1,
    FOREIGN KEY (careerCode) REFERENCES Career(code)
);

CREATE TABLE Book (
    code VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publicationDate DATE,
    publisher VARCHAR(255),
    availableCopies INT NOT NULL,
    status TINYINT(1) DEFAULT 1
);

CREATE TABLE Loan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loanDate DATE NOT NULL,
    returnDate DATE,
    status ENUM('active', 'returned', 'lost') NOT NULL,
    totalDue DECIMAL(10, 2),
    studentId VARCHAR(10) NOT NULL,
    bookCode VARCHAR(20) NOT NULL,
    FOREIGN KEY (studentId) REFERENCES Student(id),
    FOREIGN KEY (bookCode) REFERENCES Book(code)
);

CREATE TABLE Reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservationDate DATE NOT NULL,
    status ENUM('active', 'expired', 'completed') NOT NULL,
    studentId VARCHAR(10) NOT NULL,
    bookCode VARCHAR(20) NOT NULL,
    FOREIGN KEY (studentId) REFERENCES Student(id),
    FOREIGN KEY (bookCode) REFERENCES Book(code)
);

CREATE TABLE User (
    userId BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(255) NOT NULL,
    name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    username VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    status SMALLINT
);