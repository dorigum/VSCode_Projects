package service;

import model.Member;
import model.Menu;
import model.Category;
import model.Order;
import repository.CategoryRepository;
import repository.CategoryRepositoryImpl;
import repository.MemberRepository;
import repository.MemberRepositoryImpl;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import repository.OrderRepository;
import repository.OrderRepositoryImpl;
import java.util.List;
import java.util.Map;

public class AdminServiceImpl implements AdminService {
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(MenuRepository menuRepository) {
        this(menuRepository, new MemberRepositoryImpl(), new CategoryRepositoryImpl(), new OrderRepositoryImpl());
    }

    public AdminServiceImpl(
        MenuRepository menuRepository,
        MemberRepository memberRepository,
        CategoryRepository categoryRepository,
        OrderRepository orderRepository
    ) {
        this.menuRepository = menuRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    // --- 상품(메뉴) 관리 ---
    public void registerMenu(int categoryId, String name, int price, String description) {
        menuRepository.addMenu(new Menu(categoryId, name, price, description));
    }

    public List<Menu> getMenuList() {
        return menuRepository.getAllMenus();
    }

    public void deleteMenu(long id) {
        menuRepository.deleteMenu(id);
    }

    // --- 카테고리 관리 ---
    public void addCategory(String name) {
        categoryRepository.addCategory(name);
    }

    public List<Category> getCategoryList() {
        return categoryRepository.getAllCategories();
    }

    public void deleteCategory(int id) {
        categoryRepository.deleteCategory(id);
    }

    // --- 회원 관리 ---
    public List<Member> getMemberList() {
        return memberRepository.getAllMembers();
    }

    public void deleteMember(long id) {
        memberRepository.deleteMember(id);
    }

    // --- 주문 관리 ---
    public List<Order> getOrderList() {
        return orderRepository.getAllOrders();
    }

    public void cancelOrder(long orderId) {
        if (orderRepository.cancelOrder(orderId)) {
            System.out.println("주문이 취소되었습니다.");
        } else {
            System.out.println("취소 실패: 존재하지 않는 주문이거나 이미 취소된 주문입니다.");
        }
    }

    // --- 통계 기능 ---
    public void showStatistics() {
        System.out.println("\n===== [매출 통계 리포트] =====");
        
        int totalSales = orderRepository.getTotalSales();
        System.out.printf("▶ 누적 총 매출액: %,d원\n", totalSales);

        System.out.println("\n[일별 매출 추이 (최근 7일)]");
        Map<String, Integer> dailySales = orderRepository.getDailySales();
        if (dailySales.isEmpty()) {
            System.out.println("- 데이터 없음");
        } else {
            dailySales.forEach((date, sales) -> {
                String bar = "■".repeat(Math.min(20, sales / 1000));
                System.out.printf("%s | %-20s (%,d원)\n", date, bar, sales);
            });
        }

        System.out.println("\n[카테고리별 매출 현황]");
        Map<String, Integer> categorySales = orderRepository.getSalesByCategory();
        categorySales.forEach((cat, sales) -> 
            System.out.printf("- %-10s: %,d원\n", cat, sales));

        System.out.println("\n[인기 메뉴 Top 3]");
        List<String> topMenus = orderRepository.getTopSellingMenus();
        topMenus.forEach(menu -> System.out.println("- " + menu));
    }
}
