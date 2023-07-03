package dblab.sharing_flatform.service.category;

import dblab.sharing_flatform.domain.category.Category;
import dblab.sharing_flatform.dto.category.CategoryRequestDto;
import dblab.sharing_flatform.dto.category.CategoryResponseDto;
import dblab.sharing_flatform.exception.category.CategoryNotFoundException;
import dblab.sharing_flatform.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // readAll (전체 조회 - 계층형)
    public List<CategoryResponseDto> readAll() {
        List<Category> categoryList = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return CategoryResponseDto.toList(categoryList);
    }

    // 생성
    @Transactional
    public Long create(CategoryRequestDto categoryRequestDto) {
        if (categoryRequestDto.getParentCategoryName() != null) {
            Category parentCategory = categoryRepository.findByName(categoryRequestDto.getParentCategoryName()).orElseThrow(CategoryNotFoundException::new);
            categoryRepository.save(CategoryRequestDto.toEntity(categoryRequestDto, parentCategory));
            }
        Category savedCategory = categoryRepository.save(CategoryRequestDto.toEntity(categoryRequestDto, null));

        return savedCategory.getId();
    }

    // 삭제
    @Transactional
    public void delete(String name) {
        categoryRepository.delete(categoryRepository.findByName(name).orElseThrow(CategoryNotFoundException::new));
    }

}
