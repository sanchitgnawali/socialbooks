package io.sanchit.socialbook.book;

import io.sanchit.socialbook.common.BaseEntity;
import io.sanchit.socialbook.feedback.Feedback;
import io.sanchit.socialbook.history.BookTransactionHistory;
import io.sanchit.socialbook.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {
    private String authorName;
    private String title;
    private String isbn;
    private String synopses;
    private boolean archived;
    private boolean sharable;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    List<BookTransactionHistory> bookTransactionHistories;
}
