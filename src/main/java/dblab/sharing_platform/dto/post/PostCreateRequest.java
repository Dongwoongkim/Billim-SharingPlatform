package dblab.sharing_platform.dto.post;

import dblab.sharing_platform.dto.item.ItemCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "게시글 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {

    @ApiModelProperty(value = "게시글 제목", notes = "게시글 제목을 입력해주세요", required = true)
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @ApiModelProperty(value = "게시글 내용", notes = "게시글 내용 입력해주세요", required = true)
    @Lob
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @ApiModelProperty(value = "카테고리 이름", notes = "카테고리 이름을 지정해주세요", required = true)
    @NotBlank(message = "내용을 입력해주세요.")
    private String categoryName;

    @ApiModelProperty(value = "이미지", notes = "이미지를 첨부해주세요.")
    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @ApiModelProperty(value = "물품", notes = "물품을 첨부하세요.")
    @Nullable
    private ItemCreateRequest itemCreateRequest;
}
