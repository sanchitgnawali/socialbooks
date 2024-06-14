package io.sanchit.socialbook.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {
    @Query("""
            SELECT history
            from BookTransactionHistory history
            WHERE history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);


    @Query("""
            SELECT history
            from BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
                SELECT  (COUNT(*) > 0) as isBorrowed
                FROM BookTransactionHistory history
                WHERE history.book.owner.id = :userId
                AND history.book.id = :bookId
                AND history.returnApproved = false
            """)
    boolean isBookAlreadyBorrowed(Integer bookId, Integer userId);

    @Query("""
                    select history from BookTransactionHistory history
                    where history.book.id = :bookId
                    and history.user.id = :userId
                    and history.returnApproved = false
                    and history.returned = false
            """)
    Optional<BookTransactionHistory> findBookByIdAndUserId(Integer bookId, Integer userId);

    @Query("""
                    select history from BookTransactionHistory history
                    where history.book.id = :bookId
                    and history.book.owner.id = :userId
                    and history.returnApproved = false
                    and history.returned = true
            """)
    Optional<BookTransactionHistory> findBookByIdAndOwnerId(Integer bookId, Integer userId);
}
