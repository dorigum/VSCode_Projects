package controller;

import exception.CafeKioskException;
import exception.ValidationException;
import service.MenuService;
import view.EndView;
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
        return Collections.emptyList();
    }

    public List<Menu> getLatestMenuList() {
        return Collections.emptyList();
    }

    public List<Menu> getCoffeeMenuList() {
        try {
            return menuService.getCoffeeMenuList();
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Menu> getNonCoffeeMenuList() {
        try {
            return menuService.getNonCoffeeMenuList();
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Menu> getDesertMenuList() {
        try {
            return menuService.getDesertMenuList();
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
    public int order(List<OrderItem> orderItems, Member member) {
        if (member == null) {
            System.out.println("비회원 주문");
            return 1;
        }
        System.out.println("회원 주문");
        return 1;
    }

    public List<OptionGroup> getOptionGroups(Menu menu) {
        System.out.println("옵션그룹 반환");
        return null;
    }

    public List<MenuOption> getOptions(OptionGroup optiongGroup) {
        System.out.println("옵션 목록 반환");
        return null;
    }

    public String getCategoryName(Menu menu) {
        System.out.println("카테고리 이름 반환");
        return null;
    }
}
