package dblab.sharing_flatform.repository.category;

import dblab.sharing_flatform.domain.category.Category;
import dblab.sharing_flatform.exception.category.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

import static dblab.sharing_flatform.factory.category.CategoryFactory.createCategory;
import static dblab.sharing_flatform.factory.category.CategoryFactory.createCategoryWithName;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    @PersistenceContext EntityManager em;

    @Test
    void createAndReadTest() {
        // given
        Category category = createCategory();

        // when
        Category savedCategory = categoryRepository.save(category);

        // then
        categoryRepository.findById(savedCategory.getId()).orElseThrow(CategoryNotFoundException::new);
    }

    @Test
    void readAllTest() {
        // given
        List<Category> categories = List.of("name1", "name2", "name3").stream().map(n -> createCategoryWithName(n)).collect(Collectors.toList());
        categoryRepository.saveAll(categories);

        // when
        List<Category> foundCategories = categoryRepository.findAll();

        // then
        assertThat(foundCategories.size()).isEqualTo(3);
    }

    @Test
    void deleteCascadeTest() {
        // given
        Category category1 = categoryRepository.save(createCategoryWithName("category1"));
        Category category2 = categoryRepository.save(createCategory("category2", category1));
        Category category3 = categoryRepository.save(createCategory("category3", category2));
        Category category4 = categoryRepository.save(createCategoryWithName("category4"));

        // when
        categoryRepository.delete(category1);
        clear();

        // then
        List<Category> foundCategories = categoryRepository.findAll();
        assertThat(foundCategories.size()).isEqualTo(1);
        assertThat(foundCategories.get(0).getId()).isEqualTo(category4.getId());
    }

    @Test
    void findAllWithParentOrderByParentIdAscNullsFirstCategoryIdAscTest() {
        // given
        // 1		NULL
        // 2		1
        // 3		1
        // 4		2
        // 5		2
        // 6		2
        // 7		3
        // 8		NULL
        Category category1 = categoryRepository.save(createCategoryWithName("category1"));
        Category category2 = categoryRepository.save(createCategory("category2", category1));
        Category category3 = categoryRepository.save(createCategory("category3", category1));
        Category category4 = categoryRepository.save(createCategory("category4", category2));
        Category category5 = categoryRepository.save(createCategory("category5", category2));
        Category category6 = categoryRepository.save(createCategory("category6", category2));
        Category category7 = categoryRepository.save(createCategory("category7", category3));
        Category category8 = categoryRepository.save(createCategoryWithName("category8"));
        clear();

        // when
        List<Category> result = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

        // then
        // 1     NULL
        // 8     NULL
        // 2      1
        // 3      1
        // 4      2
        // 5      2
        // 6      2
        // 7      3
        assertThat(result.get(0).getId()).isEqualTo(category1.getId());
        assertThat(result.get(1).getId()).isEqualTo(category8.getId());
        assertThat(result.get(2).getId()).isEqualTo(category2.getId());
        assertThat(result.get(3).getId()).isEqualTo(category3.getId());
        assertThat(result.get(4).getId()).isEqualTo(category4.getId());
        assertThat(result.get(5).getId()).isEqualTo(category5.getId());
        assertThat(result.get(6).getId()).isEqualTo(category6.getId());
        assertThat(result.get(7).getId()).isEqualTo(category7.getId());
        assertThat(result.size()).isEqualTo(8);

    }
    private void clear() {
        em.flush();
        em.clear();
    }
}