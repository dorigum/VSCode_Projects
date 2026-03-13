package repository;

import model.MenuOption;
import java.util.List;

public interface MenuOptionRepository {
    List<MenuOption> findAll();
    MenuOption findById(long optionId);
    List<MenuOption> findByGroupId(long groupId);
    void save(MenuOption menuOption);
    void update(MenuOption menuOption);
    void delete(long optionId);
}
