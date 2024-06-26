package io.sanchit.socialbook.book;

import io.sanchit.socialbook.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;


    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest bookRequest,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.save(bookRequest, authentication));
    }

    @GetMapping
    @RequestMapping("{id}")
    public BookResponse findBookById(@PathVariable Integer id) {
        return bookService.findById(id);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication) {

        PageResponse<BookResponse> allBooks = bookService.findAllBooks(page, size, authentication);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication) {

        PageResponse<BookResponse> allBooks = bookService.findAllBooksByOwner(page, size, authentication);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication) {

        PageResponse<BorrowedBookResponse> allBooks = bookService.findAllBorrowedBooks(page, size, authentication);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication) {

        PageResponse<BorrowedBookResponse> allBooks = bookService.findAllReturnedBooks(page, size, authentication);
        return ResponseEntity.ok(allBooks);
    }

    @PatchMapping("sharable/{book-id}")
    public ResponseEntity<Integer> updateSharableBook(
            @PathVariable("book-id") Integer bookId,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.updateBookSharable(bookId, authentication));
    }

    @PatchMapping("archived/{book-id}")
    public ResponseEntity<Integer> updateArchive(
            @PathVariable("book-id") Integer bookId,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, authentication));
    }

    @PatchMapping("borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.borrowBook(bookId, authentication));
    }

    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, authentication));
    }

    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> returnApproveBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication authentication) {

        return ResponseEntity.ok(bookService.returnApproveBorrowedBook(bookId, authentication));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCover(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @PathVariable("file") MultipartFile file,
            Authentication authentication
    ) {

        bookService.uploadBookCover(bookId, file, authentication);

        return ResponseEntity.accepted().build();
    }
}
