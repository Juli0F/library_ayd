package com.ayd.library.repository;

import com.ayd.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByStatus(Boolean status);
}