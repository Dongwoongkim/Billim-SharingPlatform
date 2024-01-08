package dblab.sharing_platform.service.category;

import dblab.sharing_platform.domain.category.Category;
import dblab.sharing_platform.dto.category.CategoryCreateRequest;
import dblab.sharing_platform.dto.category.CategoryDto;
import dblab.sharing_platform.exception.category.CategoryNotFoundException;
import dblab.sharing_platform.repository.category.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> readAllCategory() {
        return CategoryDto.toList(categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc());
    }

    @Transactional
    public Long createCategory(CategoryCreateRequest request) {
        if (request.getParentCategoryName() != null) {
            Category parentCategory = categoryRepository.findByName(request.getParentCategoryName())
                    .orElseThrow(CategoryNotFoundException::new);
            Category category = categoryRepository.save(CategoryCreateRequest.toEntity(request, parentCategory));

            return category.getId();
        }

        Category category = categoryRepository.save(CategoryCreateRequest.toEntity(request, null));
        return category.getId();
    }

    @Transactional
    public void deleteCategoryByCategoryName(String categoryName) {
        categoryRepository.delete(categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryNotFoundException::new));
    }
}
