package io.sanchit.socialbook.feedback;

import io.sanchit.socialbook.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@NoArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {


    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            Authentication authentication
    ) {
        return ResponseEntity.ok(feedbackService.save(feedbackRequest, authentication));
    }

    @GetMapping
    public ResponseEntity<PageResponse<FeedbackResponse>> getFeedbacks(
            @PathVariable("book-id") int bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication authentication

    ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBookId(bookId, page, size, authentication));
    }

}
