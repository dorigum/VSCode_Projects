package service;

import model.Category;
import model.Member;
import model.Menu;
import model.Order;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 메뉴 관리
    void registerMenu(int categoryId, String name, int price, String description);

    List<Menu> getMenuList();

    void deleteMenu(long id);

    // 카테고리 관리
    void addCategory(String name);

    List<Category> getCategoryList();

    void deleteCategory(int id);

    // 회원 관리
    List<Member> getMemberList();

    void deleteMember(long id);

    // 주문 관리
    List<Order> getOrderList();

    void cancelOrder(long orderId);

    // 통계 기능
    int getTotalSales();

    Map<String, Integer> getSalesByCategory();

    List<String> getTopSellingMenus();

    Map<String, Integer> getDailySales();
}
