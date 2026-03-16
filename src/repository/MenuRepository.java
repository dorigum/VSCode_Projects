package repository;

import model.Menu;

import java.util.List;

public interface MenuRepository {
	boolean addMenu(Menu menu);

	List<Menu> getAllMenus();

	Menu findById(long id);

	boolean deleteMenu(long id);

	boolean updateMenu(Menu menu);

	List<Menu> getMenusByCategoryName(String categoryName);

	List<Menu> getMenusByCategoryId(int categoryId);

	List<Menu> getLatestMenus(int limit);

	List<Menu> getPopularMenus(int limit);

	void addOptionGroupToMenu(long menuId, long groupId, int displayOrder);

	void removeOptionGroupFromMenu(long menuId, long groupId);
}
