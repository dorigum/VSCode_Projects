package view;

import model.Category;
import model.Member;
import model.Menu;
import model.Order;
import model.OrderItem;
import model.Wishlist;
import model.OptionGroup;

import java.util.List;
import java.util.Map;

public final class EndView {
    private EndView() {
    }

    public static void success(String message) {
        System.out.println(message);
    }

    public static void printCategories(List<Category> categories) {
        printList("[카테고리 목록]", categories, "카테고리가 없습니다.");
    }

    public static void printMenus(List<Menu> menus) {
        printList("[메뉴 목록]", menus, "메뉴가 없습니다.");
    }

    public static void printMembers(List<Member> members) {
        printList("[회원 목록]", members, "회원 정보가 없습니다.");
    }

    public static void printOrders(List<Order> orders) {
        printList("[주문 목록]", orders, "주문 내역이 없습니다.");
    }

    public static void printWishlist(Member member, List<Wishlist> wishlists) {
        System.out.println("\n===== 찜 목록 조회 =====");
        if (wishlists == null || wishlists.isEmpty()) {
            System.out.println(member.getPhone() + "님의 찜한 메뉴가 없습니다.");
            return;
        }
        wishlists.forEach(System.out::println);
    }

    public static void printOrderHistory(Member member, List<OrderItem> history) {
        System.out.println("\n===== " + member.getPhone() + "님의 주문 내역 =====");
        if (history == null || history.isEmpty()) {
            System.out.println("주문 내역이 없습니다.");
            return;
        }
        history.forEach(System.out::println);
    }

    public static void printQuickOrder(Member member, List<OrderItem> orderItems) {
        System.out.println("\n===== 퀵오더 조회 =====");
        System.out.println(member.getPhone() + "님의 최근 주문 정보입니다.");
        if (orderItems == null || orderItems.isEmpty()) {
            System.out.println("이전 주문 내역이 없어 퀵오더를 사용할 수 없습니다.");
            return;
        }

        int total = 0;
        for (OrderItem item : orderItems) {
            System.out.printf("- %-15s %d개 x %,d원\n", item.getMenuNameSnapshot(), item.getQuantity(), item.getUnitPrice());
            total += item.getQuantity() * item.getUnitPrice();
        }
        System.out.printf("합계: %,d원\n", total);
    }

    public static void printLoginSuccess(Member member) {
        System.out.println("\n로그인 성공! 환영합니다, " + member.getPhone() + "님.");
        System.out.printf("보유 포인트: %,d원\n", member.getPointBalance());
    }

    public static void printSalesReport(int totalSales, Map<String, Integer> dailySales,
                                       Map<String, Integer> categorySales, List<String> topMenus) {
        System.out.println("\n===== [매출 통계 리포트] =====");
        System.out.printf("▶ 누적 총 매출액: %,d원\n", totalSales);

        System.out.println("\n[일별 매출 추이 (최근 7일)]");
        if (dailySales == null || dailySales.isEmpty()) {
            System.out.println("- 데이터 없음");
        } else {
            dailySales.forEach((date, sales) -> {
                int barLength = Math.min(20, Math.max(0, sales / 1000));
                String bar = "#".repeat(barLength);
                System.out.printf("%s | %-20s (%,d원)\n", date, bar, sales);
            });
        }

        System.out.println("\n[카테고리별 매출 현황]");
        if (categorySales == null || categorySales.isEmpty()) {
            System.out.println("- 데이터 없음");
        } else {
            categorySales.forEach((cat, sales) -> System.out.printf("- %-10s: %,d원\n", cat, sales));
        }

        System.out.println("\n[인기 메뉴 Top 3]");
        if (topMenus == null || topMenus.isEmpty()) {
            System.out.println("- 데이터 없음");
        } else {
            topMenus.forEach(menu -> System.out.println("- " + menu));
        }
    }

    public static void printList(String title, List<?> list, String emptyMessage) {
        System.out.println("\n" + title);
        if (list == null || list.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        list.forEach(System.out::println);
    }

    public static void printMenu(List<Menu> menus){
        // 메뉴를 보여주는 메서드
    }

    public static void printCart(List<OrderItem> cart){
        // 주문에 담긴 카트를 보여주는 메서드
    }

    public static void printOptionGroup(OptionGroup optionGroup){
        // 옵션그룹을 차례롤 보여줌
    }
}
