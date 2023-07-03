package dblab.sharing_flatform.dto.category;

import dblab.sharing_flatform.domain.category.Category;
import dblab.sharing_flatform.helper.FlatListToHierarchicalHelper;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "전체 카테고리 조회 응답")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponseDto {

    private Long id;
    private String name;
    private List<CategoryResponseDto> children;

    public static List<CategoryResponseDto> toList(List<Category> categoryList) {
        FlatListToHierarchicalHelper helper = FlatListToHierarchicalHelper.newInstance(
                categoryList,
                c -> CategoryResponseDto.toDto(c),
                c -> c.getParent(),
                c -> c.getId(),
                d -> d.getChildren());
        return helper.convert();
    }

    public static CategoryResponseDto toDto(Category category) {
        return new CategoryResponseDto(category.getId(),
                category.getName(),
                new ArrayList<>());
    }
}
