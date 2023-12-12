package dblab.sharing_platform.repository.likepost;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.likepost.LikePost;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.image.QPostImage.postImage;
import static dblab.sharing_platform.domain.likepost.QLikePost.likePost;

public class QLikePostRepositoryImpl extends QuerydslRepositorySupport implements QLikePostRepository {

    private final JPAQueryFactory query;
    private static final String DEFAULT_IMAGE_NAME = "testImage.jpg";

    public QLikePostRepositoryImpl(JPAQueryFactory query) {
        super(LikePost.class);
        this.query = query;
    }

    @Override
    public Page<PostDto> findAllLikesByCurrentUsername(PostPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateLikes(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicateLikes(PostPagingCondition cond){
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getUsername())) {
            builder.and(likePost.member.username.eq(cond.getUsername()));
        }
        return builder;
    }

    private List<PostDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query
                        .select(constructor(PostDto.class,
                                likePost.post.id,
                                likePost.post.title,
                                likePost.post.item.price,
                                likePost.post.member.nickname,
                                postImage.uniqueName.coalesce(DEFAULT_IMAGE_NAME),
                                likePost.post.complete,
                                likePost.post.createdTime))
                        .from(likePost)
                        .leftJoin(likePost.post.postImages, postImage)
                        .where(predicate)
                        .orderBy(likePost.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(likePost.count()).from(likePost).where(predicate).fetchOne();
    }
}
