package dblab.sharing_platform.controller.review;

import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import dblab.sharing_platform.dto.review.ReviewPagingCondition;
import dblab.sharing_platform.dto.review.ReviewRequest;
import dblab.sharing_platform.service.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Review Controller", tags = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation(value = "현재 로그인한 유저의 리뷰를 조회", notes = "현재 로그인한 유저에게 작성된 리뷰를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity findAllReviewsWriteByCurrentUser(@Valid ReviewPagingCondition condition) {
        condition.setUsername(getCurrentUsernameCheck());
        return new ResponseEntity(reviewService.findCurrentUserReviews(condition), OK);
    }

    @ApiOperation(value = "모든 리뷰 조회 (ADMIN 권한)", notes = "(ADMIN 권한으로) 작성된 모든 리뷰를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity findAllReviewWriteByAdmin(@Valid ReviewPagingCondition condition) {
        return new ResponseEntity(reviewService.findAllReviewsWriteByAdmin(condition), OK);
    }

    @ApiOperation(value = "특정 회원에 대한 리뷰를 페이징", notes = "특정 회원에 대한 리뷰를 페이징합니다.")
    @GetMapping
    public ResponseEntity findAllReviewByNickname(@Valid ReviewPagingCondition condition) {
        return new ResponseEntity(reviewService.findAllReviewsByNickname(condition), OK);
    }

    @ApiOperation(value = "거래에 대한 리뷰 생성", notes = "거래에 대한 리뷰를 생성합니다.")
    @PostMapping("/{tradeId}")
    public ResponseEntity writeReviewByTradeId(
            @ApiParam(value = "생성할 리뷰의 Trade Id", required = true) @PathVariable Long tradeId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        reviewService.writeReviewByTradeId(reviewRequest, tradeId, getCurrentUsernameCheck());
        return new ResponseEntity(CREATED);
    }

    @ApiOperation(value = "거래에 대해 작성한 리뷰 삭제", notes = "현재 로그인한 유저에게 작성된 리뷰를 조회합니다.")
    @DeleteMapping("/{tradeId}")
    public ResponseEntity deleteReviewTradeId(
            @ApiParam(value = "삭제할 리뷰의 Trade Id", required = true) @PathVariable Long tradeId) {
        reviewService.deleteReviewByTradeId(tradeId);
        return new ResponseEntity(OK);
    }
}
