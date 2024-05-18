package com.ayd.library.service;

import com.ayd.library.dto.BookRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.model.Book;
import com.ayd.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    final BookRepository repository;

    @Transactional
    public Book createBook(BookRequestDto bookDto) throws DuplicatedEntityException{
        if(repository.findById(bookDto.getCode()).isPresent())
            throw new DuplicatedEntityException("Existe un libro con el codigo: "+bookDto.getCode());
        Book book = Book.builder()
                .code(bookDto.getCode())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .publicationDate(bookDto.getPublicationDate())
                .availableCopies(bookDto.getAvailableCopies())
                .publisher(bookDto.getPublisher())
                .status(true)
                .build();

        return repository.save(book);

    }
    @Transactional
    public Book updateBook(String code, BookRequestDto updatedBook) throws NotFoundException {
        return repository.findById(code)
                .map(existingBook -> {
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
                    existingBook.setPublisher(updatedBook.getPublisher());

                    return repository.save(existingBook);
                })
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + code));
    }

    public Book getBookByCode(String code) throws NotFoundException {
        return repository.findById(code)
                .orElseThrow(() -> new NotFoundException("No se encuentra el libro con el codigo: " + code));
    }

    public List<Book> getAllBooks() {
        return repository.findByStatus(true);
    }

    @Transactional
    public Book softDeleteBook(String code) throws NotFoundException {
        Book book = repository.findById(code)
                .orElseThrow(() -> new NotFoundException("No se encuentra el libro con el codigo: " + code));
        book.setStatus(false);
        return repository.save(book);
    }
}
