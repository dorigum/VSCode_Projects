package service;

import model.Menu;
import java.util.List;

public interface MenuService {
    List<Menu> getMenusByCategory(String categoryName);

    List<Menu> getCoffeeMenuList();

    List<Menu> getNonCoffeeMenuList();

    List<Menu> getDesertMenuList();
}
