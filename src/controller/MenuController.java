package controller;

import exception.CafeKioskException;
import exception.ValidationException;
import service.MenuService;
import view.EndView;
import view.FailView;
import java.util.List;
import model.Menu;
import model.OptionGroup;
import model.Option;

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
        return null;
    }

    public List<Menu> getLatestMenuList() {
        return null;
    }

    public List<Menu> getCoffeeMenuList() {
        return null;
    }

    public List<Menu> getNonCoffeeMenuList() {
        return null;
    }

    public List<Menu> getDesertMenuList() {
        return null;
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

    public List<Option> getOptions(OptionGroup optiongGroup) {
        System.out.println("옵션 목록 반환");
        return null;
    }

    public String getCategoryName(Menu menu){
        System.out.println("카테고리 이름 반환");
        return null;
    }

    // public Member login(String phone, String password) {
    // try {
    // Member member = memberService.login(phone, password);
    // EndView.printLoginSuccess(member);
    // return member;
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // return null;
    // }
    // }

    // public void showOrderHistory(Member member) {
    // try {
    // EndView.printOrderHistory(member, memberService.getOrderHistory(member));
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // }
    // }

    // public void showWishlist(Member member) {
    // try {
    // EndView.printWishlist(member, memberService.getWishlist(member));
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // }
    // }

    // public void addWishlist(Member member, long menuId) {
    // try {
    // memberService.addWishlist(member, menuId);
    // EndView.success("찜 목록에 추가되었습니다.");
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // }
    // }

    // public void removeWishlist(long wishlistId) {
    // try {
    // memberService.removeWishlist(wishlistId);
    // EndView.success("찜이 삭제되었습니다.");
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // }
    // }

    // public void showQuickOrder(Member member) {
    // try {
    // EndView.printQuickOrder(member, memberService.getQuickOrder(member));
    // } catch (CafeKioskException e) {
    // FailView.fail(e.getMessage());
    // }
    // }
}
