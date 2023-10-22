package dblab.sharing_platform.dto.review;

import dblab.sharing_platform.domain.review.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private Long id;
    private Long tradeId;
    private String content;
    private String writer;

    public static ReviewResponse toDto(Review review) {
        if (review != null) {
            return new ReviewResponse(
                    review.getId(),
                    review.getTrade().getId(),
                    review.getContent(),
                    review.getWriter().getNickname());
        } else {
            return null;
        }
    }
}
