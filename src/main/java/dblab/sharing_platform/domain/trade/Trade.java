package dblab.sharing_platform.domain.trade;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.domain.review.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "render_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member renderMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member borrowerMember;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean tradeComplete;
    private boolean writtenReview;

    public Trade(Member renderMember, Member borrowerMember,  LocalDate startDate, LocalDate endDate, Post post) {
        this.renderMember = renderMember;
        this.borrowerMember = borrowerMember;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tradeComplete = false;
        this.writtenReview = false;
        this.post = post;
    }

    public void addReview(Review review) {
        if (this.review == null) {
            this.writtenReview = true;
            this.review = review;
        }
    }

    public void isTradeComplete(boolean value){
        this.tradeComplete = value;
        this.getPost().completed(true);
    }

    public void deleteReview(){
        if (this.review != null) {
            this.review = null;
            this.writtenReview = false;
        }
        this.getPost().completed(false);
    }
}

