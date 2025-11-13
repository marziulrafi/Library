package com.example.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.library.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")  // Base path for all methods
public class BookController {
    private List<Book> books = new ArrayList<>();  // In-memory storage (like ArrayList from Collections)
    private Long nextId = 1L;

    @GetMapping  // GET /books
    public List<Book> getAllBooks() {
        return books;
    }

    @GetMapping("/{id}")  // GET /books/1
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = books.stream().filter(b -> b.getId().equals(id)).findFirst();
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping  // POST /books (needs body)
    public Book addBook(@RequestBody Book book) {
        book.setId(nextId++);
        books.add(book);
        return book;
    }

    // (Add PUT and DELETE similarly—demo quickly)
}