package dblab.sharing_platform.dto.image;

import static java.util.stream.Collectors.toList;

import dblab.sharing_platform.domain.image.PostImage;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageDto {

    private Long id;
    private String uniqueName;
    private String originName;

    public static PostImageDto toDto(PostImage postImage) {
        return new PostImageDto(postImage.getId(),
                postImage.getUniqueName(),
                postImage.getOriginName());
    }

    public static List<PostImageDto> toDtoList(List<PostImage> postImages) {
        return postImages.stream().map(i -> PostImageDto.toDto(i)).collect(toList());
    }
}
