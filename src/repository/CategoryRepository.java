package repository;

import model.Category;

import java.util.List;

public interface CategoryRepository {
    boolean addCategory(String name);

    Category getCategoryById(int id);

    List<Category> getAllCategories();

    boolean deleteCategory(int id);
}
