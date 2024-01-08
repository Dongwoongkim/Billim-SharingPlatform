package dblab.sharing_platform.repository.review;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.review.QReview.review;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.review.Review;
import dblab.sharing_platform.dto.review.ReviewDto;
import dblab.sharing_platform.dto.review.ReviewPagingCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class QReviewRepositoryImpl extends QuerydslRepositorySupport implements QReviewRepository {
    
    private final JPAQueryFactory query;

    public QReviewRepositoryImpl(JPAQueryFactory query) {
        super(Review.class);
        this.query = query;
    }

    @Override
    public Page<ReviewDto> findAllByUsername(ReviewPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByNickname(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<ReviewDto> findAllWithMemberByCurrentUsername(ReviewPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByCurrentUsername(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<ReviewDto> findAllReviews(ReviewPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateAll();
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicateByNickname(ReviewPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getNickname())) {
            builder.and(review.writer.nickname.eq(cond.getNickname()));
        }
        return builder;
    }

    private Predicate createPredicateByCurrentUsername(ReviewPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getUsername())) {
            builder.and(review.member.username.eq(cond.getUsername()));
        }
        return builder;
    }

    private Predicate createPredicateAll() {
        BooleanBuilder builder = new BooleanBuilder();
        return builder;
    }

    private List<ReviewDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query.select(constructor(ReviewDto.class,
                                review.id,
                                review.trade.id,
                                review.member.nickname,
                                review.writer.nickname,
                                review.content))
                        .from(review)
                        .join(review.writer)
                        .where(predicate)
                        .orderBy(review.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(review.count())
                .from(review)
                .where(predicate)
                .fetchOne();
    }
}
