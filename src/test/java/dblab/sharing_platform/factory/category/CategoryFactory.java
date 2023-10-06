package dblab.sharing_platform.factory.category;

import dblab.sharing_platform.domain.category.Category;

public class CategoryFactory {
    public static Category  createCategory() {
        return new Category("category1", null);
    }
    public static Category createCategory(String name, Category parent) {
        return new Category(name, parent);
    }

    public static Category createCategoryWithName(String name) {
        return new Category(name, null);
    }


}
