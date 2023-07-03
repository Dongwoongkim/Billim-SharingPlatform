package dblab.sharing_flatform.repository.category;

import dblab.sharing_flatform.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 정렬 우선순위
    // parent_id 기준 null 우선 정렬 -> null이 여러개인 경우 category_id 오름차순
    @Query("select c from Category c left join c.parent p order by p.id asc nulls first, c.id asc")
    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

    Optional<Category> findByName(String categoryName);

}
