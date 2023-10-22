package dblab.sharing_platform.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import dblab.sharing_platform.domain.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {

    private Long id;
    private String title;
    private Long itemPrice;
    private String nickname;
    private String link;
    private boolean complete;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdTime;

    public static PostDto toDto(Post post) {
        return new PostDto(post.getId(),
                post.getTitle(),
                post.getItem() != null ? post.getItem().getPrice() : null,
                post.getMember().getNickname(),
                post.getPostImages().isEmpty() ? "testImage.jpg" : post.getPostImages().get(0).getUniqueName(),
                post.isComplete(),
                post.getCreatedTime());
    }

    public static List<PostDto> toDtoList(List<Post> posts) {
        return posts.stream().map(i -> PostDto.toDto(i)).collect(Collectors.toList());
    }
}
