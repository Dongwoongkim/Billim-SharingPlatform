package dblab.sharing_platform.controller.post;

import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.amazonaws.services.s3.AmazonS3Client;
import dblab.sharing_platform.dto.post.PostCreateRequest;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import dblab.sharing_platform.dto.post.PostUpdateRequest;
import dblab.sharing_platform.service.post.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Post Controller", tags = "Post")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final AmazonS3Client amazonS3Client;

    @ApiOperation(value = "게시글 검색", notes = "검색조건에 따라 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity readAllPostByCond(@Valid PostPagingCondition condition) {
        return new ResponseEntity(postService.readAllPostByCond(condition), OK);
    }

    @ApiOperation(value = "게시글 단건 조회", notes = "게시글 ID로 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity readSinglePostByPostId(
            @ApiParam(value = "조회할 게시글 id", required = true) @PathVariable Long postId) {
        return new ResponseEntity(postService.readSinglePostByPostId(postId), OK);
    }

    @ApiOperation(value = "본인 작성 게시글 조회", notes = "현재 로그인한 유저가 작성한 게시글을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity readAllPostWriteByCurrentUser(@Valid PostPagingCondition condition) {
        condition.setUsername(getCurrentUsernameCheck());
        return new ResponseEntity(postService.readAllWriteByCurrentUser(condition), OK);
    }

    @ApiOperation(value = "본인의 좋아요 누른 게시글 전체 조회", notes = "현재 사용자가 좋아요 누른 게시글을 전체 조회합니다.")
    @GetMapping("/likes")
    public ResponseEntity readAllLikePostByCurrentUser(@Valid PostPagingCondition condition) {
        condition.setUsername(getCurrentUsernameCheck());
        return new ResponseEntity(postService.readAllLikePostByCurrentUser(condition), OK);
    }

    @ApiOperation(value = "게시글 생성", notes = "게시글을 생성합니다.")
    @PostMapping
    public ResponseEntity createPost(@Valid @ModelAttribute PostCreateRequest postCreateRequest) {
        return new ResponseEntity(postService.createPost(postCreateRequest, getCurrentUsernameCheck()), CREATED);
    }

    @ApiOperation(value = "게시글 삭제", notes = "해당 번호의 게시글을 삭제한다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePostByPostId(
            @ApiParam(value = "삭제할 게시글 id", required = true) @PathVariable Long postId) {
        postService.deletePostByPostId(postId);
        return new ResponseEntity(OK);
    }

    @ApiOperation(value = "게시글 수정", notes = "해당 번호의 게시글을 수정한다.")
    @PatchMapping("/{postId}")
    public ResponseEntity updatePostByPostId(@ApiParam(value = "수정할 게시글 id", required = true) @PathVariable Long postId,
                                             @Valid @ModelAttribute PostUpdateRequest postUpdateRequest) {
        return new ResponseEntity(postService.updatePost(postId, postUpdateRequest), OK);
    }

    @ApiOperation(value = "게시글 좋아요/좋아요 취소", notes = "해당 번호의 게시글에 좋아요 남기기/좋아요 취소")
    @PostMapping("/{postId}/likes")
    public ResponseEntity likeUpOrDown(@ApiParam(value = "좋아요할 게시글 id", required = true) @PathVariable Long postId) {
        postService.like(postId, getCurrentUsernameCheck());
        return new ResponseEntity(OK);
    }
}
