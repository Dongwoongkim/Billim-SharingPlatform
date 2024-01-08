package dblab.sharing_platform.domain.post;

import static dblab.sharing_platform.helper.PostImageHelper.addImages;
import static dblab.sharing_platform.helper.PostImageHelper.updateImage;

import dblab.sharing_platform.domain.base.BaseTime;
import dblab.sharing_platform.domain.category.Category;
import dblab.sharing_platform.domain.embedded.item.Item;
import dblab.sharing_platform.domain.image.PostImage;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.dto.post.PostUpdateRequest;
import dblab.sharing_platform.dto.post.PostUpdateResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer likes;

    @Column(nullable = false)
    private boolean complete;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @Embedded
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages;

    public Post(String title, String content, Category category, Item item, List<PostImage> postImages, Member member) {
        this.title = title;
        this.content = content;
        this.likes = 0;
        this.category = category;
        this.item = item;
        this.member = member;
        this.complete = false;
        this.postImages = new ArrayList<>();

        addImages(postImages, this.postImages, this);
    }

    public PostUpdateResponse updatePost(PostUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();

        if (request.getItemUpdateRequest() != null) {
            this.item.updateItem(request.getItemUpdateRequest());
        }

        Map<String, List<PostImage>> m = updateImage(request, this.postImages, this);
        return PostUpdateResponse.toDto(request, this, m);
    }

    public void likeUp() {
        this.likes++;
    }

    public void likeDown() {
        this.likes--;
    }

    public void completed(boolean value) {
        this.complete = value;
    }
}
