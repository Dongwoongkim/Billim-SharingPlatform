package dblab.sharing_platform.repository.likepost;

import dblab.sharing_platform.domain.likepost.LikePost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePostRepository extends JpaRepository<LikePost, Long>, QLikePostRepository {

    void deleteByMemberIdAndPostId(Long memberId, Long postId);

    List<LikePost> findAllByMemberId(Long memberId);

    List<LikePost> findAllByPostId(Long postId);
}
