package dblab.sharing_platform.repository.review;

import dblab.sharing_platform.config.querydsl.QuerydslConfig;
import dblab.sharing_platform.domain.category.Category;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.domain.review.Review;
import dblab.sharing_platform.domain.trade.Trade;
import dblab.sharing_platform.repository.category.CategoryRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.repository.trade.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

import static dblab.sharing_platform.factory.category.CategoryFactory.createCategory;
import static dblab.sharing_platform.factory.member.MemberFactory.createBorrowerMember;
import static dblab.sharing_platform.factory.member.MemberFactory.createRenderMember;
import static dblab.sharing_platform.factory.post.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class ReviewRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ReviewRepository reviewRepository;

    Post post;
    Trade trade;
    Member member;
    Member borrowerMember;
    Category category;
    Review review;


    @BeforeEach
    public void beforeEach(){
        postRepository.deleteAll();
        tradeRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();
        reviewRepository.deleteAll();
    }

    // 멤버에 대한 글 하나가 만들어 진거임.
    public void reviewInit(){
        // 대여자
        member = createRenderMember();
        memberRepository.save(member);
        clear();

        // 대출자
        borrowerMember = createBorrowerMember();
        memberRepository.save(borrowerMember);
        clear();

        // 카테고리
        category = createCategory();
        categoryRepository.save(category);
        clear();

        // 글
        post = createPost(category, member);
        postRepository.save(post);
        clear();

        // 거래 생성
        trade = new Trade(member, borrowerMember, LocalDate.now(), LocalDate.now(), post);
        tradeRepository.save(trade);
        clear();

        // 리뷰 생성
        review = new Review("테스트 리뷰입니다.", member, borrowerMember, trade);
        trade.addReview(review);
        reviewRepository.save(review);
        clear();
    }

    @Test
    @DisplayName("리뷰 저장")
    public void reviewSaveTest(){
        // given
        reviewInit();

        // then
        assertThat(reviewRepository.count()).isEqualTo(1);
        assertThat(reviewRepository.findById(trade.getReview().getId()).get().getContent()).isEqualTo("테스트 리뷰입니다.");
    }

//    @Test
//    @DisplayName("회원 삭제 -> 회원이 받은 리뷰 삭제 By cascade")
//    public void deleteCascadeMember(){
//        //given
//        reviewInit();
//
//        // when
//        memberRepository.delete(member);
//        clear();
//
//        // then
//        assertThat(reviewRepository.count()).isEqualTo(0);
//    }

    @Test
    @DisplayName("회원 삭제 -> 회원이 작성한 리뷰 삭제 By cascade")
    public void deleteCascadeBorrowerMember(){
        //given
        reviewInit();

        // when
        memberRepository.delete(borrowerMember);
        clear();

        // then
        assertThat(reviewRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 삭제")
    public void deleteReviewTest(){
        // given
        reviewInit();

        // when
        trade.deleteReview();
        reviewRepository.delete(review);
        clear();

        // then
        assertThat(reviewRepository.count()).isEqualTo(0);
        assertThat(trade.getReview()).isNull();
    }

    public void clear(){
        em.flush();
        em.clear();
    }
}