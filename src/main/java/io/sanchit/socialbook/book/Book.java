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
    @OneToMany(mappedBy = "book")
    List<BookTransactionHistory> bookTransactionHistories;
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

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }

        var rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        return Math.round(rate * 10.0) / 10.0;
    }
}
