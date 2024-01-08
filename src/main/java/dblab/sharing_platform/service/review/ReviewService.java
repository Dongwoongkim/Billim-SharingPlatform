package dblab.sharing_platform.service.review;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.notification.NotificationType;
import dblab.sharing_platform.domain.review.Review;
import dblab.sharing_platform.domain.trade.Trade;
import dblab.sharing_platform.dto.review.PagedReviewListDto;
import dblab.sharing_platform.dto.review.ReviewPagingCondition;
import dblab.sharing_platform.dto.review.ReviewRequest;
import dblab.sharing_platform.dto.review.ReviewResponse;
import dblab.sharing_platform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.review.ExistReviewException;
import dblab.sharing_platform.exception.review.ImpossibleWriteReviewException;
import dblab.sharing_platform.exception.review.ReviewNotFoundException;
import dblab.sharing_platform.exception.trade.TradeNotCompleteException;
import dblab.sharing_platform.exception.trade.TradeNotFoundException;
import dblab.sharing_platform.helper.NotificationHelper;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.review.ReviewRepository;
import dblab.sharing_platform.repository.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    public static final String REVIEW_COMPLETE_MESSAGE = "님이 거래에 대한 리뷰를 작성했습니다.";

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TradeRepository tradeRepository;
    private final NotificationHelper notificationHelper;

    @Transactional
    public ReviewResponse writeReviewByTradeId(ReviewRequest reviewRequest, Long tradeId, String username) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(TradeNotFoundException::new);
        Member writer = memberRepository.findByUsername(username)
                .orElseThrow(AuthenticationEntryPointException::new); // 리뷰 작성자
        Member member = memberRepository.findById(trade.getRenderMember().getId())
                .orElseThrow(MemberNotFoundException::new); // 리뷰 받는 사람

        validateWriteReview(username, trade);

        Review review = new Review(reviewRequest.getContent(),
                member,
                writer,
                trade);

        reviewRepository.save(review);
        trade.addReview(review);

        notificationHelper.notificationIfSubscribe(writer, member, NotificationType.REVIEW, REVIEW_COMPLETE_MESSAGE);
        return ReviewResponse.toDto(review);
    }

    @Transactional
    public void deleteReviewByTradeId(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(TradeNotFoundException::new);
        
        if (trade.getReview() != null) {
            Review review = reviewRepository.findById(trade.getReview().getId())
                    .orElseThrow(ReviewNotFoundException::new);
            trade.deleteReview();
            reviewRepository.delete(review);
        }
    }

    public PagedReviewListDto findAllReviewsWriteByAdmin(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllReviews(cond));
    }

    public PagedReviewListDto findCurrentUserReviews(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllWithMemberByCurrentUsername(cond));
    }

    public PagedReviewListDto findAllReviewsByNickname(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllByUsername(cond));
    }

    private void validateWriteReview(String username, Trade trade) {
        if (username.equals(trade.getRenderMember().getUsername())) {
            throw new ImpossibleWriteReviewException();
        }
        if (trade.isTradeComplete() == false) {
            throw new TradeNotCompleteException();
        }
        if (trade.isWrittenReview()) {
            throw new ExistReviewException();
        }
    }
}
