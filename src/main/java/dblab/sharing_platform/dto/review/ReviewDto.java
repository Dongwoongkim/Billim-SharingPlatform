package dblab.sharing_platform.dto.review;

import dblab.sharing_platform.domain.review.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDto {

    private Long reviewId;
    private Long tradeId;
    private String receiver;
    private String writer;
    private String content;

    public static ReviewDto toDto(Review review) {
        return new ReviewDto(review.getId(), review.getTrade().getId(), review.getMember().getNickname(),
                review.getWriter().getNickname(), review.getContent());
    }
}
