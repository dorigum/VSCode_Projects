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
        if ("ADMIN".equals(member.getRole())) {
            System.out.println("\n[관리자 모드] 환영합니다, " + member.getPhone() + " 관리자님.");
        } else {
            System.out.println("\n로그인 성공! 환영합니다, " + member.getPhone() + "님.");
            System.out.printf("보유 포인트: %,d원\n", member.getPointBalance());
        }
    }

    public static void printDateSalesReport(String periodTitle, int totalSales, Map<String, Integer> periodSales) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      📅 [" + periodTitle + "]      ");
        System.out.println("=".repeat(40));
        System.out.printf("▶ 누적 총 매출액: %,d원\n", totalSales);
        System.out.println("-".repeat(40));

        if (periodSales == null || periodSales.isEmpty()) {
            System.out.println("  - 데이터 없음");
        } else {
            periodSales.forEach((period, sales) -> {
                int barLength = Math.min(30, Math.max(0, sales / 2000));
                String bar = "■".repeat(barLength);
                System.out.printf("%12s | %-30s (%,d원)\n", period, bar, sales);
            });
        }
        System.out.println("=".repeat(40));
    }

    public static void printCategorySalesReport(Map<String, Integer> categorySales) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      📂 [카테고리별 매출 분석]      ");
        System.out.println("=".repeat(40));

        if (categorySales == null || categorySales.isEmpty()) {
            System.out.println("  - 데이터 없음");
        } else {
            int total = categorySales.values().stream().mapToInt(Integer::intValue).sum();
            categorySales.forEach((cat, sales) -> {
                double percent = (total > 0) ? (sales * 100.0 / total) : 0;
                int barLength = (int) (percent / 3);
                String bar = "■".repeat(barLength);
                System.out.printf("  %-10s: %,10d원 (%5.1f%%) %s\n", cat, sales, percent, bar);
            });
            System.out.println("-".repeat(40));
            System.out.printf("  합계      : %,10d원 (100.0%%)\n", total);
        }
        System.out.println("=".repeat(40));
    }

    public static void printMenuSalesReport(List<String> topMenus) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      🏆 [메뉴별 판매 순위]      ");
        System.out.println("=".repeat(40));

        if (topMenus == null || topMenus.isEmpty()) {
            System.out.println("  - 데이터 없음");
        } else {
            for (int i = 0; i < topMenus.size(); i++) {
                System.out.printf("  %2d위. %s\n", i + 1, topMenus.get(i));
            }
        }
        System.out.println("=".repeat(40));
    }

    public static void printList(String title, List<?> list, String emptyMessage) {
        System.out.println("\n" + title);
        if (list == null || list.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        list.forEach(System.out::println);
    }

    public static void printMenu(List<Menu> menus) {
        System.out.println("\n===== 메뉴 목록 =====");
        if (menus == null || menus.isEmpty()) {
            System.out.println("표시할 메뉴가 없습니다.");
            return;
        }

        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            String availability = menu.isAvailable() ? "판매중" : "품절";
            String category = menu.getCategoryName() == null ? "" : " [" + menu.getCategoryName() + "]";
            System.out.printf("%2d. %-20s | %,d원 | %s%s%n", i + 1, menu.getMenuName(), menu.getPrice(), availability, category);
            if (menu.getDescription() != null && !menu.getDescription().trim().isEmpty()) {
                System.out.printf("    - %s%n", menu.getDescription());
            }
        }
        System.out.println(" 0. 뒤로");
    }
    public static void printCart(List<OrderItem> cart){
        // 주문에 담긴 카트를 보여주는 메서드
    }

    public static void printOptionGroups(List<OptionGroup> optionGroups) {
        System.out.println("\n[옵션 그룹 목록]");
        if (optionGroups == null || optionGroups.isEmpty()) {
            System.out.println("등록된 옵션 그룹이 없습니다.");
            return;
        }
        for (int i = 0; i < optionGroups.size(); i++) {
            System.out.printf("%d. %s (ID: %d)\n", i + 1, optionGroups.get(i).getGroupName(), optionGroups.get(i).getGroupId());
        }
    }

    public static void printMenuOptions(OptionGroup group, List<model.MenuOption> options) {
        System.out.println("\n[" + group.getGroupName() + " 세부 옵션 목록]");
        if (options == null || options.isEmpty()) {
            System.out.println("등록된 세부 옵션이 없습니다.");
            return;
        }
        System.out.printf("%-5s %-15s %-10s %-5s\n", "번호", "옵션명", "추가금액", "순서");
        System.out.println("-".repeat(40));
        for (int i = 0; i < options.size(); i++) {
            model.MenuOption opt = options.get(i);
            System.out.printf("%-5d %-15s %+,8d원 %5d\n", i + 1, opt.getOptionName(), opt.getExtraPrice(), opt.getDisplayOrder());
        }
    }

    public static void printOptionGroup(OptionGroup optionGroup){
        System.out.println("\n" + optionGroup.getGroupName() + "을(를) 선택해 주세요.");
    }
}



