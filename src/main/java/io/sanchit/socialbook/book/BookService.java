package io.sanchit.socialbook.book;

import io.sanchit.socialbook.book.common.PageResponse;
import io.sanchit.socialbook.file.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

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

    public Integer updateArchivedStatus(Integer bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("User doesn't have permission to perform this action.");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);

        return book.getId();
    }

    public Integer borrowBook(Integer bookId, Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("Archived or Not Sharable books can't be borrowed.");
        }

        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("User can't borrow book they own.");
        }

        boolean isBookAlreadyBorrowed = bookTransactionHistoryRepository.isBookAlreadyBorrowed(bookId, user.getId());

        if (isBookAlreadyBorrowed) {
            throw new OperationNotPermittedException("Book is already borrowed.");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("Archived or Not Sharable books can't be returned.");
        }

        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("User can't return book they own.");
        }

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findBookByIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("User can't return book they never borrowed."));

        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnApproveBorrowedBook(Integer bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("Archived or Not Sharable books can't be returned.");
        }

        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("User can't return book they own.");
        }

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findBookByIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book hasn't been returned yet."));

        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCover(Integer bookId, MultipartFile file, Authentication authentication) {

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book with id %d not found.".formatted(bookId)));

        User user = (User) authentication.getPrincipal();

        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);

    }
}
