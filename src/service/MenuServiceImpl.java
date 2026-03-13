package service;

import exception.ValidationException;
import exception.NotFoundException;
import model.Menu;
import model.MenuOption;
import model.OptionGroup;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import repository.OptionGroupRepository;
import repository.OptionGroupRepositoryImpl;
import repository.MenuOptionRepository;
import repository.MenuOptionRepositoryImpl;
import java.util.List;

public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final MenuOptionRepository menuOptionRepository;

    public MenuServiceImpl() {
        this(
            new MenuRepositoryImpl(),
            new OptionGroupRepositoryImpl(),
            new MenuOptionRepositoryImpl()
        );
    }

    public MenuServiceImpl(MenuRepository menuRepository) {
        this(menuRepository, new OptionGroupRepositoryImpl(), new MenuOptionRepositoryImpl());
    }

    public MenuServiceImpl(MenuRepository menuRepository, OptionGroupRepository optionGroupRepository,
                          MenuOptionRepository menuOptionRepository) {
        if (menuRepository == null) {
            throw new ValidationException("MenuRepository는 null일 수 없습니다.");
        }
        if (optionGroupRepository == null) {
            throw new ValidationException("OptionGroupRepository는 null일 수 없습니다.");
        }
        if (menuOptionRepository == null) {
            throw new ValidationException("MenuOptionRepository는 null일 수 없습니다.");
        }
        this.menuRepository = menuRepository;
        this.optionGroupRepository = optionGroupRepository;
        this.menuOptionRepository = menuOptionRepository;
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
    public List<OptionGroup> getOptionGroups(Menu menu) {
        if (menu == null) {
            throw new ValidationException("메뉴 정보가 없습니다.");
        }
        if (menu.getMenuId() <= 0) {
            throw new ValidationException("유효하지 않은 메뉴입니다.");
        }
        return optionGroupRepository.findOptionGroupsWithOptionsByMenuId(menu.getMenuId());
    }

    @Override
    public List<MenuOption> getOptionsByGroup(OptionGroup optionGroup) {
        if (optionGroup == null) {
            throw new ValidationException("옵션 그룹 정보가 없습니다.");
        }
        if (optionGroup.getGroupId() <= 0) {
            throw new ValidationException("유효하지 않은 옵션 그룹입니다.");
        }
        return menuOptionRepository.findByGroupId(optionGroup.getGroupId());
    }
}
