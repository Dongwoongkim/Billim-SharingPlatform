package dblab.sharing_flatform.domain.image;

import dblab.sharing_flatform.domain.post.Post;
import dblab.sharing_flatform.exception.image.NoExtException;
import dblab.sharing_flatform.exception.image.UnSupportExtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    private static final String extension[] = {"jpg", "jpeg", "gif", "bmp", "png"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Image(String originName) {
        this.originName = originName;
        this.uniqueName = extractExtAndGenerateUniqueName(originName);
    }

    // Image -> Post 양방향 매핑정보
    public void initPost(Post post) {
        if (this.post == null) {
            this.post = post;
        }
    }

    public void cancel(Post post) {
        if (this.post != null) {
            this.post = null;
        }
    }

    private String extractExtAndGenerateUniqueName(String originName) {
        String ext = getExt(originName);
        return UUID.randomUUID().toString() + "." + ext;
    }

    private String getExt(String originName) {
        try {
            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            if (supportFormat(ext)) {
                return ext;
            } else {
                throw new UnSupportExtException();
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw new NoExtException();
        } catch (UnSupportExtException e) {
            throw e;
        }
    }

    private boolean supportFormat(String ext) {
        return Arrays.stream(extension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }
}
