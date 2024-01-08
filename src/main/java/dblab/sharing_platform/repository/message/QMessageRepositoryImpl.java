package dblab.sharing_platform.repository.message;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.message.QMessage.message;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.message.Message;
import dblab.sharing_platform.dto.message.MessageDto;
import dblab.sharing_platform.dto.message.MessagePagingCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

public class QMessageRepositoryImpl extends QuerydslRepositorySupport implements QMessageRepository {

    private final JPAQueryFactory query;

    public QMessageRepositoryImpl(JPAQueryFactory query) {
        super(Message.class);
        this.query = query;
    }

    @Override
    public Page<MessageDto> findAllBySendMember(MessagePagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateBySender(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<MessageDto> findAllByReceiverMember(MessagePagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByReceiver(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicateBySender(MessagePagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(cond.getSenderUsername())) {
            builder.and(message.sendMember.username.eq(cond.getSenderUsername()));
        }

        if (StringUtils.hasText(cond.getReceiverName())) {
            builder.and(message.receiveMember.nickname.eq(cond.getReceiverName()));
        }

        builder.and(message.deleteBySender.eq(false));
        return builder;
    }

    private Predicate createPredicateByReceiver(MessagePagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(cond.getReceiverUsername())) {
            builder.and(message.receiveMember.username.eq(cond.getReceiverUsername()));
        }

        if (StringUtils.hasText(cond.getSenderName())) {
            builder.and(message.sendMember.nickname.eq(cond.getSenderName()));
        }

        builder.and(message.deleteByReceiver.eq(false));
        return builder;
    }


    private List<MessageDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query.select(constructor(MessageDto.class,
                                message.id,
                                message.content,
                                message.sendMember.nickname,
                                message.receiveMember.nickname,
                                message.createdTime,
                                message.checked,
                                message.post.id,
                                message.post.title))
                        .from(message)
                        .where(predicate)
                        .orderBy(message.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(message.count()).from(message).where(predicate).fetchOne();
    }
}
