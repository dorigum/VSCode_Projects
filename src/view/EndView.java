package view;

import model.Category;
import model.Member;
import model.Menu;
import model.MenuOption;
import model.OptionGroup;
import model.OptionSelection;
import model.Order;
import model.OrderItem;
import model.PointHistory;
import model.Wishlist;

import java.util.ArrayList;
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
		System.out.println("\n===== [전체 회원 정보 관리] =====");
		if (members == null || members.isEmpty()) {
			System.out.println("등록된 회원 정보가 없습니다.");
			return;
		}

		System.out.printf("%-6s | %-13s | %-12s | %-7s | %-10s\n", "ID", "전화번호", "보유 포인트", "등급", "가입일자");
		System.out.println("-".repeat(65));

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		for (Member member : members) {
			String dateStr = (member.getCreatedAt() != null) ? sdf.format(member.getCreatedAt()) : "N/A";
			System.out.printf("%-6d | %-13s | %,10d원 | %-7s | %-10s\n", 
					member.getMemberId(), 
					member.getPhone(), 
					member.getPointBalance(), 
					member.getRole(), 
					dateStr);
		}
		System.out.println("-".repeat(65));
	}

	public static void printOrders(List<Order> orders) {
		System.out.println("\n===== [전체 주문 관리 및 상세 내역] =====");
		if (orders == null || orders.isEmpty()) {
			System.out.println("주문 내역이 없습니다.");
			return;
		}

		for (Order order : orders) {
			String statusIcon = "COMPLETED".equalsIgnoreCase(order.getStatus()) ? "[V]" : "[X]";
			String statusText = "COMPLETED".equalsIgnoreCase(order.getStatus()) ? "[정상결제]" : "[취소완료]";
			String memberInfo = order.getMemberPhone() != null ? order.getMemberPhone() : "비회원";

			// 헤더 정보 (상태, ID, 주문자, 총액, 시간)
			System.out.printf("%s %-8s | 주문ID: %3d | 주문자: %-13s | 총액: %,7d원\n", 
					statusIcon, statusText, order.getOrderId(), memberInfo, order.getTotalAmount());
			System.out.printf("   주문 시간: %s\n", order.getOrderDate());

			// 상세 메뉴 정보
			if (order.getItems() != null && !order.getItems().isEmpty()) {
				for (OrderItem item : order.getItems()) {
					System.out.printf("   └─ %-15s %d개 (단가: %,d원)\n", 
							item.getMenuNameSnapshot(), item.getQuantity(), item.getUnitPrice());

					// 선택된 세부 옵션 정보 출력 (가시성 강화)
					List<MenuOption> options = item.getOptions();
					if (options != null && !options.isEmpty()) {
						String optionStr = options.stream()
								.map(MenuOption::getOptionName)
								.collect(java.util.stream.Collectors.joining(", "));
						System.out.printf("      ▶ [선택된 상세 옵션: %s]\n", optionStr);
					}
				}
			}
			System.out.println("-".repeat(80));
		}
	}

	public static void printWishlist(Member member, List<Wishlist> wishlists) {
		System.out.println("\n===== 찜 목록 조회 =====");
		if (wishlists == null || wishlists.isEmpty()) {
			System.out.println(member.getPhone() + "님의 찜한 메뉴가 없습니다.");
			return;
		}
		wishlists.forEach(System.out::println);
	}

	public static void printOrderHistory(Member member, List<Order> orders) {
		System.out.println("\n===============================");
		System.out.printf("  %s님의 주문 내역%n", member.getPhone());
		System.out.println("===============================");
		if (orders == null || orders.isEmpty()) {
			System.out.println("  주문 내역이 없습니다.");
		} else {
			orders.forEach(System.out::println);
		}
		System.out.println("===============================");
	}

	public static void printPointHistory(Member member, List<PointHistory> history) {
		System.out.println("\n===== [" + member.getPhone() + "] 님의 포인트 변동 내역 =====");
		if (history == null || history.isEmpty()) {
			System.out.println("  > 아직 포인트 변동 내역이 존재하지 않습니다.");
		} else {
			System.out.printf("%-12s | %-11s | %s\n", "일시", "변동금액", "사유");
			System.out.println("-".repeat(50));
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
			for (PointHistory h : history) {
				String amountStr = (h.getAmount() > 0 ? "+" : "") + String.format("%,d원", h.getAmount());
				System.out.printf("%-12s | %11s | %s\n", 
						sdf.format(h.getCreatedAt()), amountStr, h.getReason());
			}
		}
		System.out.println("-".repeat(50));
		System.out.printf("▶ 현재 총 보유 포인트: %,d원\n", member.getPointBalance());
	}

	public static void printQuickOrder(Member member, Order order) {
	    System.out.println("\n===============================");
	    System.out.println("         퀵오더 조회           ");
	    System.out.println("===============================");
	    System.out.printf ("  %s님의 최근 주문입니다.%n", member.getPhone());
	    System.out.println("-------------------------------");
	    if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
	        System.out.println("  이전 주문 내역이 없습니다.");
	        System.out.println("===============================");
	        return;
	    }
	    int total = 0;
	    for (OrderItem item : order.getItems()) {
	        System.out.printf("  - %-15s %d개 x %,d원%n",
	            item.getMenuNameSnapshot(), item.getQuantity(), item.getUnitPrice());
	        if (item.getOptions() != null && !item.getOptions().isEmpty()) {
	            for (MenuOption opt : item.getOptions()) {
	                System.out.printf("      └ %s%n", opt.getOptionName());
	            }
	        }
	        total += item.getQuantity() * item.getUnitPrice();
	    }
	    System.out.println("-------------------------------");
	    System.out.printf ("  합계: %,d원%n", total);
	    System.out.println("===============================");
	}

	public static void printLoginSuccess(Member member) {
		System.out.println("\n===============================");
		if ("ADMIN".equals(member.getRole())) {
			System.out.println("  관리자 모드로 로그인되었습니다.");
			System.out.printf("  %s 관리자님, 환영합니다.%n", member.getPhone());
		} else {
			System.out.printf("  환영합니다, %s님!%n", member.getPhone());
			//System.out.printf("  보유 포인트: %,d원%n", member.getPointBalance());
		}
		System.out.println("===============================");
	}

	public static void printDateSalesReport(String periodTitle, long totalSales, Map<String, Long> periodSales) {
		System.out.println("\n" + "=".repeat(45));
		System.out.println("      📅 [" + periodTitle + "]      ");
		System.out.println("=".repeat(45));
		System.out.printf("▶ 누적 총 매출액: %,d원\n", totalSales);
		System.out.println("-".repeat(45));

		if (periodSales == null || periodSales.isEmpty()) {
			System.out.println("  - 데이터 없음");
		} else {
			// 상대적 스케일링을 위해 최대 매출액 산출
			long maxSales = periodSales.values().stream().mapToLong(Long::longValue).max().orElse(1L);
			
			periodSales.forEach((period, sales) -> {
				// 최대 길이를 25로 제한하여 상대적 비율로 출력
				int barLength = (int) (sales * 25 / maxSales);
				String bar = "■".repeat(barLength);
				System.out.printf("%14s | %-25s (%,d원)\n", period, bar, sales);
			});
		}
		System.out.println("=".repeat(45));
	}

	public static void printCategorySalesReport(Map<String, Long> categorySales) {
		System.out.println("\n" + "=".repeat(40));
		System.out.println("      📂 [카테고리별 매출 분석]      ");
		System.out.println("=".repeat(40));

		if (categorySales == null || categorySales.isEmpty()) {
			System.out.println("  - 데이터 없음");
		} else {
			long total = categorySales.values().stream().mapToLong(Long::longValue).sum();
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

	public static void printDetailedPeriodReport(String start, String end, Map<String, Object> stats) {
		System.out.println("\n" + "=".repeat(40));
		System.out.println("      📅 [기간별 상세 매출 내역]      ");
		System.out.println("=".repeat(40));
		System.out.printf("▶ 기간: %s ~ %s\n", start, end);
		System.out.println("-".repeat(40));
		if (stats == null || stats.isEmpty()) {
			System.out.println("  - 해당 기간의 데이터가 없습니다.");
		} else {
			System.out.printf("  총 주문 건수 : %,d건\n", ((Number) stats.getOrDefault("count", 0L)).longValue());
			System.out.printf("  총 매출 금액 : %,d원\n", ((Number) stats.getOrDefault("amount", 0L)).longValue());
			long count = ((Number) stats.getOrDefault("count", 0L)).longValue();
			long amount = ((Number) stats.getOrDefault("amount", 0L)).longValue();
			if (count > 0) {
				System.out.printf("  객단가(AVG) : %,d원\n", amount / count);
			}
		}
		System.out.println("=".repeat(40));
	}

	public static void printHourlySalesReport(Map<Integer, Long> hourlySales) {
		System.out.println("\n" + "=".repeat(40));
		System.out.println("      🕒 [시간대별 매출 분석]      ");
		System.out.println("=".repeat(40));
		if (hourlySales == null || hourlySales.isEmpty()) {
			System.out.println("  - 데이터 없음");
		} else {
			long maxSales = hourlySales.values().stream().mapToLong(Long::longValue).max().orElse(1L);
			for (int hour = 0; hour < 24; hour++) {
				long sales = hourlySales.getOrDefault(hour, 0L);
				int barLength = (int) (sales * 25 / maxSales);
				String bar = "■".repeat(barLength);
				System.out.printf("  %02d시 | %-25s (%,d원)\n", hour, bar, sales);
			}
		}
		System.out.println("=".repeat(40));
	}

	public static void printDayOfWeekSalesReport(Map<String, Long> daySales) {
		System.out.println("\n" + "=".repeat(40));
		System.out.println("      📅 [요일별 매출 분석]      ");
		System.out.println("=".repeat(40));
		if (daySales == null || daySales.isEmpty()) {
			System.out.println("  - 데이터 없음");
		} else {
			long maxSales = daySales.values().stream().mapToLong(Long::longValue).max().orElse(1L);
			daySales.forEach((day, sales) -> {
				int barLength = (int) (sales * 25 / maxSales);
				String bar = "■".repeat(barLength);
				System.out.printf(" %-4s | %-25s (%,d원)\n", day, bar, sales);
			});
		}
		System.out.println("=".repeat(40));
	}

	public static void printTopMemberReport(List<Map<String, Object>> topMembers) {
		System.out.println("\n" + "=".repeat(40));
		System.out.println("      💎 [우수 회원 기여도 분석]      ");
		System.out.println("=".repeat(40));
		if (topMembers == null || topMembers.isEmpty()) {
			System.out.println("  - 데이터 없음");
		} else {
			System.out.printf("  %-15s | %s\n", "회원 연락처", "누적 결제액");
			System.out.println("-".repeat(40));
			for (int i = 0; i < topMembers.size(); i++) {
				Map<String, Object> m = topMembers.get(i);
				System.out.printf("  %d. %-15s | %,d원\n", i + 1, m.get("phone"), m.get("total"));
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
			String category = menu.getCategoryName() == null ? "" : "[" + menu.getCategoryName() + "] ";
			System.out.printf("▶ [메뉴 ID: %d] %s%-20s | %,d원 | %s%n", menu.getMenuId(), category, menu.getMenuName(),
					menu.getPrice(), availability);

			List<OptionGroup> groups = menu.getOptionGroups();
			if (groups != null && !groups.isEmpty()) {
				String options = groups.stream().map(OptionGroup::getGroupName)
						.collect(java.util.stream.Collectors.joining(", "));
				System.out.println("    └─ 선택 가능한 옵션: " + options);
			}

			if (menu.getDescription() != null && !menu.getDescription().trim().isEmpty()) {
				System.out.printf("    - %s%n", menu.getDescription());
			}
		}
	}

	public static void printOrderMenu(List<Menu> menus) {
		System.out.println("\n===== 메뉴 목록 =====");
		if (menus == null || menus.isEmpty()) {
			System.out.println("표시할 메뉴가 없습니다.");
			return;
		}

		for (int i = 0; i < menus.size(); i++) {
			Menu menu = menus.get(i);
			String category = menu.getCategoryName() == null ? "" : "[" + menu.getCategoryName() + "] ";
			System.out.printf(" %d. %s%-20s | %,d원%n", i + 1, category, menu.getMenuName(), menu.getPrice());

			List<OptionGroup> groups = menu.getOptionGroups();
			if (groups != null && !groups.isEmpty()) {
				String options = groups.stream().map(OptionGroup::getGroupName)
						.collect(java.util.stream.Collectors.joining(", "));
				System.out.println("    선택 가능한 옵션: " + options);
			}

			if (menu.getDescription() != null && !menu.getDescription().trim().isEmpty()) {
				System.out.printf("    - %s%n", menu.getDescription());
			}
		}
		System.out.println(" 0. 뒤로");
		System.out.println(" 8. 카트확인");
		System.out.println(" 9. 주문하기");
	}

	public static void printCartManagementMenu() {
		System.out.println("\n===== 장바구니 관리 =====");
		System.out.println("1. 상품 삭제");
		System.out.println("2. 수량 변경");
		System.out.println("3. 장바구니 비우기");
		System.out.println("9. 주문하기");
		System.out.println("0. 뒤로");
	}

	public static void printCart(List<OrderItem> cart) {
		System.out.println("\n===== 장바구니 조회 =====");
		if (cart == null || cart.isEmpty()) {
			System.out.println("현재 장바구니가 비어 있습니다.");
			return;
		}

		int total = 0;
		for (int i = 0; i < cart.size(); i++) {
			OrderItem item = cart.get(i);
			int itemTotal = item.getQuantity() * item.getUnitPrice();
			total += itemTotal;

			System.out.printf("%d. %s (%d개) - 단가: %,d원, 금액: %,d원%n", i + 1, item.getMenuNameSnapshot(),
					item.getQuantity(), item.getUnitPrice(), itemTotal);

			if (item.getCategoryNameSnapshot() != null && !item.getCategoryNameSnapshot().isBlank()) {
				System.out.printf("   카테고리: %s%n", item.getCategoryNameSnapshot());
			}

			List<MenuOption> options = item.getOptions();
			if (options != null && !options.isEmpty()) {
				System.out.printf("   선택 옵션: %s%n", formatSelectedOptions(options));
			}
		}
		System.out.printf("합계: %,d원%n", total);
	}

	public static void printOptionGroups(List<OptionGroup> optionGroups) {
		System.out.println("\n[옵션 그룹 목록]");
		if (optionGroups == null || optionGroups.isEmpty()) {
			System.out.println("등록된 옵션 그룹이 없습니다.");
			return;
		}
		for (int i = 0; i < optionGroups.size(); i++) {
			System.out.printf("%d. %s (ID: %d)\n", i + 1, optionGroups.get(i).getGroupName(),
					optionGroups.get(i).getGroupId());
		}
	}

	public static void printMenuOptions(OptionGroup group, List<MenuOption> options) {
		System.out.println("\n[" + group.getGroupName() + " 세부 옵션 목록]");
		if (options == null || options.isEmpty()) {
			System.out.println("등록된 세부 옵션이 없습니다.");
			return;
		}
		System.out.printf("%-5s %-15s %-10s %-5s\n", "번호", "옵션명", "추가금액", "순서");
		System.out.println("-".repeat(40));
		for (int i = 0; i < options.size(); i++) {
			MenuOption opt = options.get(i);
			System.out.printf("%-5d %-15s %+,8d원 %5d\n", i + 1, opt.getOptionName(), opt.getExtraPrice(),
					opt.getDisplayOrder());
		}
	}

	public static void printOptionGroup(OptionGroup optionGroup) {
		System.out.print("\n" + optionGroup.getGroupName() + " ");
	}

	public static void printSelectedOptionGroups(List<OptionGroup> optionGroups, OptionSelection selection) {
		System.out.println("\n옵션을 선택해 주세요.");
		if (optionGroups == null || optionGroups.isEmpty()) {
			System.out.println("등록된 옵션 그룹이 없습니다.");
			return;
		}
		System.out.println(formatOptionGroups(optionGroups, selection));
	}

	public static void printSelectableMenuOptions(OptionGroup optionGroup, OptionSelection selection) {
		List<MenuOption> options = optionGroup.getOptions();
		System.out.println("\n" + optionGroup.getGroupName() + " 옵션:");
		if (options == null || options.isEmpty()) {
			System.out.println("옵션 목록이 없습니다.");
			return;
		}

		for (int i = 0; i < options.size(); i++) {
			MenuOption option = options.get(i);
			String selectedMark = selection.isSelected(optionGroup.getGroupId(), option.getOptionId()) ? " (선택)" : "";
			System.out.printf("%d. %s%s%n", i + 1, option.getOptionName(), selectedMark);
		}
	}

	private static String formatOptionGroups(List<OptionGroup> optionGroups, OptionSelection selection) {
		List<String> formattedGroups = new ArrayList<>();
		for (int i = 0; i < optionGroups.size(); i++) {
			OptionGroup optionGroup = optionGroups.get(i);
			formattedGroups.add(formatOptionGroup(optionGroup, selection, i + 1));
		}
		return String.join(" | ", formattedGroups);
	}

	private static String formatOptionGroup(OptionGroup optionGroup, OptionSelection selection, int groupIndex) {
		List<MenuOption> options = optionGroup.getOptions();
		List<String> formattedOptions = new ArrayList<>();
		if (options != null) {
			for (int i = 0; i < options.size(); i++) {
				MenuOption option = options.get(i);
				String selectedMark = selection.isSelected(optionGroup.getGroupId(), option.getOptionId()) ? "(선택)"
						: "";
				formattedOptions.add(option.getOptionName() + ":" + (i + 1) + selectedMark);
			}
		}
		return optionGroup.getGroupName() + "(그룹 " + groupIndex + "): " + String.join(", ", formattedOptions);
	}

	private static String formatSelectedOptions(List<MenuOption> options) {
		List<String> optionNames = new ArrayList<>();
		for (MenuOption option : options) {
			optionNames.add(option.getOptionName());
		}
		return String.join(", ", optionNames);
	}
}
