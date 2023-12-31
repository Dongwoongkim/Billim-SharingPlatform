package dblab.sharing_platform.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import dblab.sharing_platform.domain.comment.Comment;
import dblab.sharing_platform.helper.FlatListToHierarchicalHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDto {

    private Long commentId;
    private String nickname;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "자식 댓글 리스트")
    private List<CommentDto> children;

    @SuppressWarnings("unchecked")
    public static List<CommentDto> toDtoList(List<Comment> replies) {
        FlatListToHierarchicalHelper helper = FlatListToHierarchicalHelper.newInstance(
                replies,
                r -> CommentDto.toDto(r),
                r -> r.getParent(),
                r -> r.getId(),
                d -> d.getChildren());
        return helper.convert();
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                        comment.getId(),
                        comment.getMember() == null ? "(알수없음)" : comment.getMember().getNickname(),
                        comment.getContent(),
                        comment.getCreatedTime(),
                        new ArrayList<>());
    }
}
