package dblab.sharing_flatform.dto.category;

import dblab.sharing_flatform.domain.category.Category;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "카테고리 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryRequestDto {

    @ApiModelProperty(value = "카테고리 명", notes = "카테고리명을 입력해주세요.", required = true, example = "Essay")
    @NotBlank(message = "카테고리명은 반드시 입력하셔야 합니다.")
    private String name;

    @ApiModelProperty(value = "상위 카테고리 명", notes = "상위 카테고리명을 입력해주세요.", required = true, example = "Book")
    private String parentCategoryName;

    public static Category toEntity(CategoryRequestDto categoryRequestDto, Category parent) {
        return new Category(categoryRequestDto.getName(), parent);
    }
}
