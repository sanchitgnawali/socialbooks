package io.sanchit.socialbook.feedback;

import io.sanchit.socialbook.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest feedbackRequest) {
        return Feedback.builder()
                .note(feedbackRequest.note())
                .comment(feedbackRequest.comments())
                .book(Book.builder().id(feedbackRequest.bookId()).build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownComment(Objects.equals(userId, feedback.getCreatedBy()))
                .build();
    }
}
