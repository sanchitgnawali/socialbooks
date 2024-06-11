package io.sanchit.socialbook.book;

import io.sanchit.socialbook.book.common.PageResponse;
import io.sanchit.socialbook.history.BookTransactionHistory;
import io.sanchit.socialbook.history.BookTransactionHistoryRepository;
import io.sanchit.socialbook.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Integer save(BookRequest bookRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found.".formatted(id)));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse>
    findAllBorrowedBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());

        List<BorrowedBookResponse> allBorrowedBooks = bookTransactionHistories.stream()
                .map(bookMapper::toBorrowedBookResponse).toList();


        return new PageResponse<>(
                allBorrowedBooks,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories
                = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());

        List<BorrowedBookResponse> allReturnedBooks = bookTransactionHistories.stream()
                .map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                allReturnedBooks,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast()
        );
    }

    public Integer updateBookSharable(Integer bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("User doesn't have permission to perform this action.");
        }
        book.setSharable(!book.isSharable());
        bookRepository.save(book);

        return book.getId();
    }
}
