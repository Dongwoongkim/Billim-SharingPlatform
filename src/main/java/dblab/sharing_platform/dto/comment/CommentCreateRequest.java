package dblab.sharing_platform.dto.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "댓글 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {

    @ApiModelProperty(value = "댓글 내용", notes = "댓글 내용을 입력해주세요", required = true, example = "안녕하세요.")
    @NotBlank(message = "댓글 내용을 입력해주세요")
    private String content;

    @ApiModelProperty(value = "부모 댓글 아이디", notes = "부모 댓글 아이디를 입력해주세요", example = "7")
    private Long parentCommentId;
}
