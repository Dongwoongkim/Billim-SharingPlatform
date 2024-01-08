package dblab.sharing_platform.repository.post;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.image.QPostImage.postImage;
import static dblab.sharing_platform.domain.post.QPost.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
public class QPostRepositoryImpl extends QuerydslRepositorySupport implements QPostRepository {

    private static final String DEFAULT_IMAGE = "testImage.jpg";

    private final JPAQueryFactory query;

    public QPostRepositoryImpl(JPAQueryFactory query) {
        super(Post.class);
        this.query = query;
    }

    @Override
    public Page<PostDto> findAllByCategoryAndTitle(PostPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicate(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<PostDto> findAllWithMemberByCurrentUsername(PostPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateByCurrentUsername(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicate(PostPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(cond.getTitle())) {
            builder.and(post.title.containsIgnoreCase(cond.getTitle()));
        }

        if (StringUtils.hasText(cond.getCategoryName())) {
            builder.and(post.category.name.equalsIgnoreCase(cond.getCategoryName()));
        }

        return builder;
    }

    private Predicate createPredicateByCurrentUsername(PostPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getUsername())) {
            builder.and(post.member.username.eq(cond.getUsername()));
        }
        return builder;
    }

    private List<PostDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query.select(constructor(PostDto.class,
                                post.id,
                                post.title,
                                post.item.price,
                                post.member.nickname,
                                postImage.uniqueName.coalesce(DEFAULT_IMAGE),
                                post.complete,
                                post.createdTime))
                        .from(post)
                        .leftJoin(post.postImages, postImage)
                        .where(predicate)
                        .orderBy(post.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(post.count()).from(post).where(predicate).fetchOne();
    }
}
