package dblab.sharing_platform.repository.post;

import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import org.springframework.data.domain.Page;


public interface QPostRepository {

    // 게시글 검색 및 페이징 ( Querydsl 사용 )
    // 검색조건 : 카테고리 name + 글 제목 -> Paging
    Page<PostDto> findAllByCategoryAndTitle(PostPagingCondition cond);

    Page<PostDto> findAllWithMemberByCurrentUsername(PostPagingCondition cond);

}