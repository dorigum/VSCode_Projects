package service;

import model.Menu;
import model.MenuOption;
import model.OptionGroup;

import java.util.List;

public interface MenuService {
    List<Menu> getMenusByCategory(String categoryName);

    List<OptionGroup> getOptionGroups(Menu menu);

    List<MenuOption> getOptionsByGroup(OptionGroup optionGroup);
}
