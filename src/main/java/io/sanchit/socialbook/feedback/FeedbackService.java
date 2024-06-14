package io.sanchit.socialbook.feedback;

import io.sanchit.socialbook.book.Book;
import io.sanchit.socialbook.book.BookRepository;
import io.sanchit.socialbook.book.OperationNotPermittedException;
import io.sanchit.socialbook.book.common.PageResponse;
import io.sanchit.socialbook.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedbackRequest feedbackRequest, Authentication authentication) {
        Book book = bookRepository.findById(feedbackRequest.bookId()).orElseThrow(
                () -> new EntityNotFoundException("No book with id %s found".formatted(feedbackRequest.bookId())));

        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The book is not sharable or is archived.");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(user.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("You can't provide feedback to the book you own.");
        }

        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        return feedbackRepository.save(feedback).getId();

    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBookId(int bookId,
                                                                   int page,
                                                                   int size,
                                                                   Authentication authentication) {
        Pageable pageable = PageRequest.of(size, page);
        User user = (User) authentication.getPrincipal();

        Page<Feedback> allFeedBacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = allFeedBacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId())).toList();

        return new PageResponse<>(
                feedbackResponses,
                allFeedBacks.getNumber(),
                allFeedBacks.getSize(),
                allFeedBacks.getTotalElements(),
                allFeedBacks.getTotalPages(),
                allFeedBacks.isFirst(),
                allFeedBacks.isLast()
        );
    }
}
