package service;

import model.*;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 메뉴 관리
    void registerMenu(int categoryId, String name, int price, String description);

    List<Menu> getMenuList();

    void updateMenu(long menuId, int categoryId, String name, int price, String description, boolean isAvailable);

    void deleteMenu(long id);

    void addOptionGroupToMenu(long menuId, long groupId, int displayOrder);

    void removeOptionGroupFromMenu(long menuId, long groupId);

    // 카테고리 관리
    void addCategory(String name);

    List<Category> getCategoryList();

    void deleteCategory(int id);

    Category getCategoryById(int id);

    // 옵션 그룹 관리
    List<OptionGroup> getOptionGroupList();

    void addOptionGroup(String name);

    void deleteOptionGroup(long groupId);

    // 메뉴 옵션 관리
    List<MenuOption> getMenuOptionsByGroup(long groupId);

    void addMenuOption(long groupId, String name, int extraPrice, int displayOrder);

    void updateMenuOption(long optionId, String name, int extraPrice, int displayOrder);

    void deleteMenuOption(long optionId);

    // 회원 관리
    List<Member> getMemberList();

    void deleteMember(long id);

    void updateMemberRole(long id, String newRole);

    void updateMemberPoint(long id, int amount, String reason);

    // 주문 관리
    List<Order> getOrderList();

    void cancelOrder(long orderId);

    // 통계 기능
    long getTotalSales();

    Map<String, Long> getSalesByCategory();

    List<String> getTopSellingMenus();

    Map<String, Long> getDailySales();

    Map<String, Long> getSalesByPeriod(String format);

    // 260313 [feat]: 매출 통계 고도화 신규 메서드
    Map<String, Object> getSalesStatsByPeriod(String startDate, String endDate);
    Map<Integer, Long> getHourlySales();
    Map<String, Long> getDayOfWeekSales();
    List<Map<String, Object>> getTopSpenders(int limit);

    void exportStatisticsToCSV();
}
