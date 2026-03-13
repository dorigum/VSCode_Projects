package repository;

import model.Menu;

import java.util.List;

public interface MenuRepository {
    boolean addMenu(Menu menu);

    List<Menu> getAllMenus();

    Menu findById(long id);

    boolean deleteMenu(long id);

    List<Menu> getMenusByCategoryName(String categoryName);

    void addOptionGroupToMenu(long menuId, long groupId, int displayOrder);
}
