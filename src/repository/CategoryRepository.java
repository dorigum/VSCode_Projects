package repository;

import model.Category;
import java.util.List;

public interface CategoryRepository {
    boolean addCategory(String name);

    Category getCategoryById(int id);

    List<Category> getAllCategories();

    boolean deleteCategory(int id);

    // 신규: 카테고리별 옵션 그룹 매핑 관리
    boolean addOptionGroupToCategory(int categoryId, long groupId, int displayOrder);
    
    boolean removeOptionGroupFromCategory(int categoryId, long groupId);
}
