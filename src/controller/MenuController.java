package controller;

import exception.CafeKioskException;
import exception.ValidationException;
import service.MenuService;
import view.FailView;
import java.util.List;
import java.util.Collections;
import model.Menu;
import model.OptionGroup;
import model.MenuOption;

import model.Member;
import model.OrderItem;

public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        if (menuService == null) {
            throw new ValidationException("MenuService는 null일 수 없습니다.");
        }
        this.menuService = menuService;
    }

    public List<Menu> getPopularMenuList() {
        try {
            return menuService.getPopularMenus();
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Menu> getLatestMenuList() {
        try {
            return menuService.getLatestMenus();
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Menu> getMenusByCategory(String categoryName) {
        try {
            return menuService.getMenusByCategory(categoryName);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     *
     * @param orderItems
     * @param member
     * @return 1: 성공, 0 실패
     */
    public int order(List<OrderItem> orderItems, Member member, int pointUsed) {
        try {
            return menuService.placeOrder(orderItems, member, pointUsed);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return 0;
        }
    }

    public List<OptionGroup> getOptionGroups(Menu menu) {
        try {
            return menuService.getOptionGroups(menu);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<MenuOption> getOptions(OptionGroup optionGroup) {
        try {
            return menuService.getOptionsByGroup(optionGroup);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public String getCategoryName(Menu menu) {
        if (menu == null) {
            return "";
        }
        return menu.getCategoryName() == null ? "" : menu.getCategoryName();
    }
}
