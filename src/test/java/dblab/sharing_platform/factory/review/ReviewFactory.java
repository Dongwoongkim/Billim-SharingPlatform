package dblab.sharing_platform.factory.review;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.review.Review;

import static dblab.sharing_platform.factory.member.MemberFactory.*;

public class ReviewFactory {

    public static Review createReview(){
        return new Review("테스트 리뷰입니다.",  createRenderMember(), createBorrowerMember(), null);
    }
    public static Review createReviewWithMember(Member member , Member reviewerMember){
        return new Review("테스트 리뷰입니다.", member, reviewerMember, null);
    }
}
