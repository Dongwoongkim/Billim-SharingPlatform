package dblab.sharing_platform.repository.trade;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.trade.QTrade.trade;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.trade.Trade;
import dblab.sharing_platform.dto.trade.TradeDto;
import dblab.sharing_platform.dto.trade.TradePagingCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

public class QTradeRepositoryImpl extends QuerydslRepositorySupport implements QTradeRepository {

    private final JPAQueryFactory query;

    public QTradeRepositoryImpl(JPAQueryFactory query) {
        super(Trade.class);
        this.query = query;
    }

    @Override
    public Page<TradeDto> findAllByCond(TradePagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicate(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<TradeDto> findAllByMyRend(TradePagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByCurrentNicknameRend(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<TradeDto> findAllByMyBorrow(TradePagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByCurrentNicknameBorrow(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicateByCurrentNicknameRend(TradePagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(trade.renderMember.username.eq(cond.getRenderMember()));
        if (cond.getTradeComplete() != null) {
            builder.and(trade.tradeComplete.eq(cond.getTradeComplete()));
        }
        return builder;
    }

    private Predicate createPredicateByCurrentNicknameBorrow(TradePagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(trade.borrowerMember.username.eq(cond.getBorrowerMember()));
        if (cond.getTradeComplete() != null) {
            builder.and(trade.tradeComplete.eq(cond.getTradeComplete()));
        }
        return builder;
    }

    private Predicate createPredicate(TradePagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getRenderMember())) {
            builder.and(trade.renderMember.nickname.eq(cond.getRenderMember()));
        }
        if (StringUtils.hasText(cond.getBorrowerMember())) {
            builder.and(trade.borrowerMember.nickname.eq(cond.getBorrowerMember()));
        }
        if (StringUtils.hasText(cond.getCategoryName())) {
            builder.and(trade.post.category.name.eq(cond.getCategoryName()));
        }
        if (cond.getTradeComplete() != null) {
            builder.and(trade.tradeComplete.eq(cond.getTradeComplete()));
        }
        if (cond.getPostId() != null) {
            builder.and(trade.post.id.eq(cond.getPostId()));
        }
        return builder;
    }

    private List<TradeDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query.select(constructor(TradeDto.class,
                                trade.id,
                                trade.post.id,
                                trade.post.title,
                                trade.renderMember.nickname,
                                trade.borrowerMember.nickname
                        ))
                        .from(trade)
                        .where(predicate)
                        .orderBy(trade.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(trade.count())
                .from(trade)
                .where(predicate)
                .fetchOne();
    }
}
