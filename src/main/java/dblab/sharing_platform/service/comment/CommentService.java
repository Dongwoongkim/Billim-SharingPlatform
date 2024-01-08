package dblab.sharing_platform.service.comment;

import dblab.sharing_platform.domain.comment.Comment;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.comment.CommentCreateRequest;
import dblab.sharing_platform.dto.comment.CommentDto;
import dblab.sharing_platform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_platform.exception.comment.CommentNotFoundException;
import dblab.sharing_platform.exception.comment.RootCommentNotFoundException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.repository.comment.CommentRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long createCommentWithPostId(Long postId, CommentCreateRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(AuthenticationEntryPointException::new);
        Comment parent =
                request.getParentCommentId() == null ? null : commentRepository.findById(request.getParentCommentId())
                        .orElseThrow(RootCommentNotFoundException::new);

        Comment createComment = commentRepository.save(
                new Comment(request.getContent(),
                        request.getParentCommentId() == null ? true : false,
                        post,
                        member,
                        parent));

        return createComment.getId();
    }

    @Transactional
    public void deleteCommentByCommentId(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
        comment.delete();
    }

    public List<CommentDto> readAllCommentByPostId(Long postId) {
        validatePostExists(postId);
        return CommentDto.toDtoList(commentRepository.findAllOrderByParentIdAscNullsFirstByPostId(postId));
    }

    private void validatePostExists(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
