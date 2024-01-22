package dblab.sharing_platform.domain.message;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    private boolean deleteBySender;

    private boolean deleteByReceiver;

    private boolean checked;

    @Column(updatable = false)
    private LocalDateTime createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiveMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public Message(String content, Member receiveMember, Member sendMember, Post post) {
        this.content = content;
        this.receiveMember = receiveMember;
        this.sendMember = sendMember;
        this.deleteBySender = false;
        this.deleteByReceiver = false;
        this.post = post;
        this.checked = false;
        this.createdTime = LocalDateTime.now();
    }

    public void deleteBySender() {
        this.deleteBySender = true;
    }

    public void deleteByReceiver() {
        this.deleteByReceiver = true;
    }

    public void readByReceiver() {
        this.checked = true;
    }

    public boolean isDeletable() {
        return isDeleteBySender() && isDeleteByReceiver();
    }


}
