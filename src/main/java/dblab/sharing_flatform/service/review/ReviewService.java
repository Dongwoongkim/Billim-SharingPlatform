package dblab.sharing_flatform.service.review;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.notification.NotificationType;
import dblab.sharing_flatform.domain.review.Review;
import dblab.sharing_flatform.domain.trade.Trade;
import dblab.sharing_flatform.dto.review.PagedReviewListDto;
import dblab.sharing_flatform.dto.review.ReviewPagingCondition;
import dblab.sharing_flatform.dto.review.ReviewRequestDto;
import dblab.sharing_flatform.dto.review.ReviewResponseDto;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.exception.review.ExistReviewException;
import dblab.sharing_flatform.exception.review.ImpossibleWriteReviewException;
import dblab.sharing_flatform.exception.review.ReviewNotFoundException;
import dblab.sharing_flatform.exception.trade.TradeNotCompleteException;
import dblab.sharing_flatform.exception.trade.TradeNotFoundException;
import dblab.sharing_flatform.helper.NotificationHelper;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.review.ReviewRepository;
import dblab.sharing_flatform.repository.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TradeRepository tradeRepository;
    private final NotificationHelper notificationHelper;

    public static final String REVIEW_COMPLETE_MESSAGE = "님이 거래에 대한 리뷰를 작성했습니다.";

    @Transactional
    public ReviewResponseDto writeReviewByTradeId(ReviewRequestDto reviewRequestDto, Long tradeId, String username) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(TradeNotFoundException::new);
        Member writer = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new); // 리뷰 작성자
        Member member = memberRepository.findById(trade.getRenderMember().getId()).orElseThrow(MemberNotFoundException::new); // 리뷰 받는 사람

        validate(username, trade);

        Review review = new Review(reviewRequestDto.getContent(),
                member,
                writer);

        reviewRepository.save(review);
        trade.addReview(review);
        notificationHelper.notificationIfSubscribe(writer, member, NotificationType.REVIEW, REVIEW_COMPLETE_MESSAGE);
        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public void deleteReviewByTradeId(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(TradeNotFoundException::new);
        Review review = reviewRepository.findById(trade.getReview().getId()).orElseThrow(ReviewNotFoundException::new);
        trade.deleteReview();

        reviewRepository.delete(review);
    }

    public PagedReviewListDto findAllReviewsWriteByAdmin(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllReviews(cond));
    }

    public PagedReviewListDto findCurrentUserReviews(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllWithMemberByCurrentUsername(cond));
    }

    public PagedReviewListDto findAllReviewsByUsername(ReviewPagingCondition cond) {
        return PagedReviewListDto.toDto(reviewRepository.findAllByUsername(cond));
    }

    private void validate(String username, Trade trade) {
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
