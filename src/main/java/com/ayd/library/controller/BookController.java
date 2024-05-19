package com.ayd.library.controller;


import com.ayd.library.dto.BookRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

     BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookRequestDto bookDto) throws DuplicatedEntityException {
        return ResponseEntity.ok(bookService.createBook(bookDto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Book> getBookByCode(@PathVariable String code) throws NotFoundException {
        return ResponseEntity.ok(bookService.getBookByCode(code));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() throws NotFoundException {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PutMapping("/{code}")
    public ResponseEntity<Book> updateBook(@PathVariable String code, @RequestBody BookRequestDto book) throws NotFoundException {
        return ResponseEntity.ok(bookService.updateBook(code, book));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Book> deleteBook(@PathVariable String code) throws NotFoundException {
        return ResponseEntity.ok(bookService.softDeleteBook(code));
    }
}