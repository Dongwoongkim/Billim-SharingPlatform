package dblab.sharing_platform.controller.post;

import dblab.sharing_platform.dto.post.PostCreateRequestDto;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import dblab.sharing_platform.dto.post.PostUpdateRequestDto;
import dblab.sharing_platform.dto.response.Response;
import dblab.sharing_platform.service.post.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(value = "Post Controller", tags = "Post")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "게시글 검색", notes = "검색조건에 따라 페이징하여 조회합니다.")
    @GetMapping
    @ResponseStatus(OK)
    public Response readAllPostByCond(@Valid PostPagingCondition cond) {
        return Response.success(OK.value(),postService.readAllPostByCond(cond));
    }
    @ApiOperation(value = "게시글 단건 조회", notes = "게시글 ID로 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    public Response readSinglePostByPostId(@ApiParam(value = "조회할 게시글 id", required = true) @PathVariable Long postId) {
        return Response.success(OK.value(),postService.readSinglePostByPostId(postId));
    }
    @ApiOperation(value = "본인 작성 게시글 조회", notes = "현재 로그인한 유저가 작성한 게시글을 조회합니다.")
    @GetMapping("/my")
    @ResponseStatus(OK)
    public Response readAllPostWriteByCurrentUser(@Valid PostPagingCondition cond){
        cond.setUsername(getCurrentUsernameCheck());
        return Response.success(OK.value(),postService.readAllWriteByCurrentUser(cond));
    }
    @ApiOperation(value ="본인의 좋아요 누른 게시글 전체 조회", notes = "현재 사용자가 좋아요 누른 게시글을 전체 조회합니다.")
    @GetMapping("/likes")
    @ResponseStatus(OK)
    public Response readAllLikePostByCurrentUser(@Valid PostPagingCondition cond) {
        cond.setUsername(getCurrentUsernameCheck());
        return Response.success(OK.value(),postService.readAllLikePostByCurrentUser(cond));
    }
    @ApiOperation(value = "게시글 생성", notes = "게시글을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response createPost(@Valid @ModelAttribute PostCreateRequestDto postCreateRequestDto) {
        return Response.success(CREATED.value(), postService.createPost(postCreateRequestDto, getCurrentUsernameCheck()));
    }
    @ApiOperation(value = "게시글 삭제", notes = "해당 번호의 게시글을 삭제한다.")
    @DeleteMapping("/{postId}")
    @ResponseStatus(OK)
    public Response deletePostByPostId(@ApiParam(value = "삭제할 게시글 id", required = true) @PathVariable Long postId) {
        postService.deletePostByPostId(postId);
        return Response.success(OK.value());
    }
    @ApiOperation(value = "게시글 수정", notes = "해당 번호의 게시글을 수정한다.")
    @PatchMapping("/{postId}")
    @ResponseStatus(OK)
    public Response updatePostByPostId(@ApiParam(value = "수정할 게시글 id", required = true) @PathVariable Long postId,
                           @Valid @ModelAttribute PostUpdateRequestDto postUpdateRequestDto) {
        return Response.success(OK.value(), postService.updatePost(postId, postUpdateRequestDto));
    }
    @ApiOperation(value = "게시글 좋아요/좋아요 취소", notes = "해당 번호의 게시글에 좋아요 남기기/좋아요 취소")
    @PostMapping("/{postId}/likes")
    @ResponseStatus(OK)
    public Response likeUpOrDown(@ApiParam(value = "좋아요할 게시글 id", required = true) @PathVariable Long postId) {
        postService.like(postId, getCurrentUsernameCheck());
        return Response.success(OK.value());
    }
}