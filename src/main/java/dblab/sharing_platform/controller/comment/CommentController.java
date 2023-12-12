package dblab.sharing_platform.controller.comment;

import dblab.sharing_platform.dto.comment.CommentCreateRequest;
import dblab.sharing_platform.service.comment.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(value = "Comment Controller", tags = "Comment")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "게시글 번호로 댓글 전체 조회", notes = "게시글 ID로 게시글의 댓글을 전체 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity readAllCommentByPostId(@ApiParam(value = "Post ID", required = true) @PathVariable Long postId) {
        return new ResponseEntity(commentService.readAllCommentByPostId(postId), OK);
    }

    @ApiOperation(value = "댓글 생성", notes = "댓글을 생성합니다.")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity createCommentWithPostId(@ApiParam(value = "Post ID", required = true) @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request) {
        return new ResponseEntity(commentService.createCommentWithPostId(postId, request, getCurrentUsernameCheck()), CREATED);
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteCommentByCommentId(@ApiParam(value = "삭제할 Comment ID", required = true) @PathVariable Long commentId) {
        commentService.deleteCommentByCommentId(commentId);
        return new ResponseEntity(OK);
    }
}
