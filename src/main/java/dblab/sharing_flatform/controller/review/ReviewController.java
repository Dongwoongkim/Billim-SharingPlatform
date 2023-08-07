package dblab.sharing_flatform.controller.review;

import dblab.sharing_flatform.dto.response.Response;
import dblab.sharing_flatform.dto.review.ReviewRequestDto;
import dblab.sharing_flatform.dto.review.ReviewPagingCondition;
import dblab.sharing_flatform.service.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static dblab.sharing_flatform.config.security.util.SecurityUtil.getCurrentUsernameCheck;

@Api(value = "Review Controller", tags = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation(value = "현재 로그인한 유저의 리뷰를 조회", notes = "현재 로그인한 유저에게 작성된 리뷰를 조회합니다.")
    @GetMapping("/myPage")
    @ResponseStatus(HttpStatus.OK)
    public Response findAllReviewsWriteByCurrentUser(@Valid ReviewPagingCondition cond){
        cond.setUsername(getCurrentUsernameCheck());
        return Response.success(reviewService.findCurrentUserReviews(cond));
    }

    @ApiOperation(value = "모든 리뷰 조회 (ADMIN 권한)", notes = "(ADMIN 권한으로) 작성된 모든 리뷰를 조회합니다.")
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Response findAllReviewWriteByAdmin(@Valid ReviewPagingCondition cond){
        return Response.success(reviewService.findAllReviewsWriteByAdmin(cond));
    }

    @ApiOperation(value = "특정 회원에 대한 리뷰를 페이징", notes = "특정 회원에 대한 리뷰를 페이징합니다.")
    @GetMapping
    public Response findAllReviewByUsername(@Valid ReviewPagingCondition cond){
        return Response.success(reviewService.findAllReviewsByUsername(cond));
    }

    @ApiOperation(value = "거래에 대한 리뷰 생성", notes = "거래에 대한 리뷰를 생성합니다.")
    @PostMapping("/{tradeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response writeReviewByTradeId(@ApiParam(value = "생성할 리뷰의 Trade Id", required = true) @PathVariable Long tradeId,
                                @Valid @RequestBody ReviewRequestDto reviewRequestDto){
        reviewService.writeReviewByTradeId(reviewRequestDto, tradeId, getCurrentUsernameCheck());
        return Response.success();
    }

    @ApiOperation(value = "거래에 대해 작성한 리뷰 삭제", notes = "현재 로그인한 유저에게 작성된 리뷰를 조회합니다.")
    @DeleteMapping("/{tradeId}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteReviewTradeId(@ApiParam(value = "삭제할 리뷰의 Trade Id", required = true) @PathVariable Long tradeId){
        reviewService.deleteReviewByTradeId(tradeId);
        return Response.success();
    }
}
