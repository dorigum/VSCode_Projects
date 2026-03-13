package service;

import exception.ValidationException;
import exception.NotFoundException;
import model.Menu;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import java.util.List;

public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;

    public MenuServiceImpl() {
        this(new MenuRepositoryImpl());
    }

    public MenuServiceImpl(MenuRepository menuRepository) {
        if (menuRepository == null) {
            throw new ValidationException("MenuRepository는 null일 수 없습니다.");
        }
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Menu> getMenusByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ValidationException("카테고리명은 비어 있을 수 없습니다.");
        }
        List<Menu> menus = menuRepository.getMenusByCategoryName(categoryName);
        if (menus == null || menus.isEmpty()) {
            throw new NotFoundException(categoryName + " 카테고리의 메뉴가 없습니다.");
        }
        return menus;
    }

    @Override
    public List<Menu> getCoffeeMenuList() {
        return getMenusByCategory("커피");
    }

    @Override
    public List<Menu> getNonCoffeeMenuList() {
        return getMenusByCategory("논커피");
    }

    @Override
    public List<Menu> getDesertMenuList() {
        return getMenusByCategory("디저트");
    }
}
