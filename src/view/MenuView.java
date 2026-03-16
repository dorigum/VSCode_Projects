package view;

import controller.AdminController;
import controller.MemberController;
import controller.MenuController;
import model.Member;
import model.OrderItem;
import model.Menu;

import java.util.Scanner;
import java.util.List;

public class MenuView {
	private static final Scanner scanner = new Scanner(System.in, "UTF-8");

	public void run(AdminController adminController, MemberController memberController, MenuController menuController) {
		while (true) {
			printMainMenu();
			int choice = readInt("메뉴 선택: ");

			if (choice == 1) {
				runMemberFlow(memberController, menuController);
			} else if (choice == 2) {
				new OrderingView(scanner).run(menuController, null);
			} else if (choice == 3) {
				runAdminFlow(adminController, memberController);
			} else if (choice == 0) {
				EndView.success("프로그램을 종료합니다.");
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
		scanner.close();
	}

	public static void close() {
		scanner.close();
	}

	private void runMemberFlow(MemberController memberController, MenuController menuController) {
		System.out.println("\n1. 로그인");
		System.out.println("2. 회원가입");
		System.out.println("0. 뒤로");
		int choice = readInt("선택: ");

		if (choice == 2) {
			String phone = readText("전화번호 (010-1234-5678): ");
			String password = readText("비밀번호 (4자리 이상): ");
			int age = readInt("나이: ");

			// 카테고리 목록 출력
			System.out.println("\n선호 카테고리를 선택해주세요.");
			System.out.println("1. 커피");
			System.out.println("2. 논커피");
			System.out.println("3. 디저트");
			System.out.println("0. 선택 안함");
			int categoryChoice = readInt("선택: ");

			int categoryId = 0;
			switch (categoryChoice) {
			case 1:
				categoryId = 1;
				break;
			case 2:
				categoryId = 2;
				break;
			case 3:
				categoryId = 3;
				break;
			default:
				categoryId = 0;
			}

			memberController.register(phone, password, age, categoryId);
			return;
		}

		String phone = readText("휴대폰 번호 (Phone): ");
		String password = readText("비밀번호: ");
		Member member = memberController.login(phone, password);

		if (member == null)
			return;

		while (true) {
			System.out.println("\n1. 주문 내역 보기");
			System.out.println("2. 퀵오더 (최근 주문 바로 주문)");
			System.out.println("3. 주문하기 (메뉴판 보기)");
			System.out.println("4. 추천 메뉴 보기");
			System.out.println("5. 선호 카테고리 변경");
			System.out.println("6. 포인트 적립 내역 확인");
			System.out.println("0. 로그아웃");
			int sub = readInt("선택: ");

			if (sub == 1) {
				memberController.showOrderHistory(member);
			} else if (sub == 2) {
				List<OrderItem> quickItems = memberController.showQuickOrder(member);
				if (quickItems != null && !quickItems.isEmpty()) {
					new OrderingView(scanner).runWithCart(menuController, member, quickItems);
				}
			} else if (sub == 3) {
				new OrderingView(scanner).run(menuController, member);
			} else if (sub == 4) {
				List<Menu> recommended = memberController.getRecommendedMenus(member);
				if (recommended == null || recommended.isEmpty()) {
					System.out.println("선호 카테고리를 먼저 설정해주세요.");
				} else {
					System.out.println("\n===== 추천 메뉴 =====");
					recommended.forEach(m -> System.out.printf("- %s | %,d원\n", m.getMenuName(), m.getPrice()));
				}
			} else if (sub == 5) {
				System.out.println("\n선호 카테고리를 선택해주세요.");
				System.out.println("1. 커피");
				System.out.println("2. 논커피");
				System.out.println("3. 디저트");
				System.out.println("0. 선택 안함");
				int categoryChoice = readInt("선택: ");
				int categoryId = 0;
				switch (categoryChoice) {
				case 1:
					categoryId = 1;
					break;
				case 2:
					categoryId = 2;
					break;
				case 3:
					categoryId = 3;
					break;
				default:
					categoryId = 0;
				}
				memberController.updatePreferredCategory(member, categoryId);
			} else if (sub == 6) {
				memberController.showPointHistory(member);
			} else if (sub == 0) {
				EndView.success("로그아웃 되었습니다.");
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runAdminFlow(AdminController adminController, MemberController memberController) {
		System.out.println("\n--- [관리자 인증] ---");
		String phone = readText("관리자 휴대폰 번호: ");
		String password = readText("관리자 비밀번호: ");

		Member admin = memberController.login(phone, password);
		if (admin == null)
			return;

		if (!"ADMIN".equals(admin.getRole())) {
			FailView.fail("관리자 권한이 없습니다.");
			return;
		}

		while (true) {
			System.out.println("\n===== [관리자 통합 관리 모드] =====");
			System.out.println("1. 카테고리 관리 (CRUD)");
			System.out.println("2. 메뉴 및 옵션 관리");
			System.out.println("3. 회원 관리 (조회/삭제)");
			System.out.println("4. 주문 관리 (조회/삭제)");
			System.out.println("5. 매출 통계 및 그래프 조회");
			System.out.println("0. 메인 메뉴로 돌아가기");

			int choice = readInt("선택: ");

			if (choice == 1) {
				runCategoryManagement(adminController);
			} else if (choice == 2) {
				runMenuAndOptionManagement(adminController);
			} else if (choice == 3) {
				runMemberManagement(adminController);
			} else if (choice == 4) {
				runOrderManagement(adminController);
			} else if (choice == 5) {
				runStatisticsManagement(adminController);
			} else if (choice == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runMenuAndOptionManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [메뉴 및 옵션 관리] ---");
			System.out.println("1. 메뉴 정보 관리");
			System.out.println("2. 메뉴 옵션 관리");
			System.out.println("0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				runMenuManagement(adminController);
			} else if (sub == 2) {
				runOptionManagement(adminController);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runOptionManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [메뉴 옵션 관리] ---");
			List<model.OptionGroup> groups = adminController.listOptionGroups();
			System.out.println("\n1. 그룹 추가 | 2. 세부 옵션 관리 | 3. 그룹 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				String name = readText("새 옵션 그룹명 (예: 온도, 사이즈) (취소: 0): ");
				if (name.equals("0"))
					continue;
				adminController.addOptionGroup(name);
			} else if (sub == 2) {
				if (groups == null || groups.isEmpty()) {
					FailView.fail("먼저 옵션 그룹을 등록해 주세요.");
					continue;
				}
				int groupIdx = readInt("관리할 그룹 번호: ");
				if (groupIdx < 1 || groupIdx > groups.size()) {
					FailView.fail("올바른 번호를 선택해 주세요.");
					continue;
				}
				runDetailOptionManagement(adminController, groups.get(groupIdx - 1));
			} else if (sub == 3) {
				if (groups == null || groups.isEmpty()) {
					FailView.fail("삭제할 옵션 그룹이 없습니다.");
					continue;
				}
				int groupIdx = readInt("삭제할 그룹 번호 (취소: 0): ");
				if (groupIdx == 0)
					continue;
				if (groupIdx < 1 || groupIdx > groups.size()) {
					FailView.fail("올바른 번호를 선택해 주세요.");
					continue;
				}
				adminController.deleteOptionGroup(groups.get(groupIdx - 1).getGroupId());
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runDetailOptionManagement(AdminController adminController, model.OptionGroup group) {
		while (true) {
			List<model.MenuOption> options = adminController.listMenuOptions(group);
			System.out.println("\n1. 옵션 추가 | 2. 옵션 수정 | 3. 옵션 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				String name = readText("옵션명: ");
				int price = readInt("추가 금액: ");
				int order = readInt("표시 순서: ");
				adminController.addMenuOption(group.getGroupId(), name, price, order);
			} else if (sub == 2) {
				if (options == null || options.isEmpty()) {
					FailView.fail("수정할 옵션이 없습니다.");
					continue;
				}
				int optIdx = readInt("수정할 옵션 번호 (취소: 0): ");
				if (optIdx == 0)
					continue;
				if (optIdx < 1 || optIdx > options.size()) {
					FailView.fail("올바른 번호를 선택해 주세요.");
					continue;
				}
				model.MenuOption target = options.get(optIdx - 1);
				String name = readText("새 옵션명 (기존: " + target.getOptionName() + "): ");
				int price = readInt("새 추가 금액 (기존: " + target.getExtraPrice() + "): ");
				int order = readInt("새 표시 순서 (기존: " + target.getDisplayOrder() + "): ");
				adminController.updateMenuOption(target.getOptionId(), name, price, order);
			} else if (sub == 3) {
				if (options == null || options.isEmpty()) {
					FailView.fail("삭제할 옵션이 없습니다.");
					continue;
				}
				int optIdx = readInt("삭제할 옵션 번호 (취소: 0): ");
				if (optIdx == 0)
					continue;
				if (optIdx < 1 || optIdx > options.size()) {
					FailView.fail("올바른 번호를 선택해 주세요.");
					continue;
				}
				adminController.deleteMenuOption(options.get(optIdx - 1).getOptionId());
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runCategoryManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [카테고리 관리] ---");
			adminController.listCategories();
			System.out.println("\n1. 추가 | 2. 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				String name = readText("새 카테고리명: ");
				adminController.addCategory(name);
			} else if (sub == 2) {
				int id = readInt("삭제할 카테고리 ID (취소: 0): ");
				if (id == 0)
					continue;
				adminController.deleteCategory(id);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runMenuManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [메뉴 정보 관리] ---");
			adminController.listMenus();
			System.out.println("\n1. 등록 | 2. 수정 | 3. 삭제 | 4. 메뉴별 전용 옵션 설정 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				while (true) {
					System.out.println("\n[현재 카테고리 목록]");
					adminController.listCategories();
					int categoryId = readInt("카테고리 ID (취소: 0): ");
					if (categoryId == 0)
						break;
					String name = readText("메뉴명: ");
					int price = readInt("가격: ");
					String description = readText("설명: ");
					
					if (adminController.registerMenu(categoryId, name, price, description)) {
						// 방금 등록된 메뉴의 ID를 가져오기 위해 목록을 다시 조회 (가장 최근 것)
						List<Menu> currentMenus = adminController.getMenuList(); // AdminController에 메서드 추가 필요
						Menu latestMenu = currentMenus.stream()
								.sorted((m1, m2) -> Long.compare(m2.getMenuId(), m1.getMenuId()))
								.findFirst().orElse(null);
						
						if (latestMenu != null) {
							System.out.println("\n[알림] 신규 메뉴에 대한 세부 옵션 설정이 필요합니까?");
							System.out.println("1. 예 (지금 바로 설정) | 0. 아니오 (나중에 설정)");
							if (readInt("선택: ") == 1) {
								runSingleMenuOptionMapping(adminController, latestMenu);
							}
						}
						break;
					}
				}
			} else if (sub == 2) {
				System.out.println("\n[수정 가능한 메뉴 목록]");
				adminController.listMenus();
				long menuId = readLong("수정할 메뉴의 [ID 번호]를 입력하세요 (취소: 0): ");
				if (menuId == 0) continue;
				
				System.out.println("\n--- [메뉴 정보 수정 시작] ---");
				adminController.listCategories();
				int categoryId = readInt("새 카테고리 ID 번호: ");
				String name = readText("새 메뉴 이름: ");
				int price = readInt("새 가격 (단위: 원): ");
				String description = readText("새 메뉴 설명: ");
				System.out.print("판매 상태 설정 (1. 판매가능, 0. 품절처리): ");
				boolean isAvailable = readInt("") == 1;
				
				adminController.updateMenu(menuId, categoryId, name, price, description, isAvailable);
			} else if (sub == 3) {
				System.out.println("\n[삭제 가능한 메뉴 목록]");
				adminController.listMenus();
				long menuId = readLong("삭제할 메뉴의 [ID 번호]를 입력하세요 (취소: 0): ");
				if (menuId == 0)
					continue;
				adminController.deleteMenu(menuId);
			} else if (sub == 4) {
				runMenuOptionMapping(adminController);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runSingleMenuOptionMapping(AdminController adminController, Menu menu) {
		while (true) {
			System.out.println("\n=== [" + menu.getMenuName() + "] 전용 옵션 설정 ===");
			System.out.println("연결할 옵션 그룹 번호를 선택해 주세요.");
			List<model.OptionGroup> allGroups = adminController.listOptionGroups();
			System.out.println("0. 설정 종료");
			
			int choice = readInt("연결할 그룹 번호: ");
			if (choice == 0) break;
			
			if (choice < 1 || choice > allGroups.size()) {
				FailView.fail("잘못된 번호입니다.");
				continue;
			}
			
			int order = readInt("표시 순서 (예: 1, 2...): ");
			adminController.addOptionGroupToMenu(menu.getMenuId(), allGroups.get(choice - 1).getGroupId(), order);
		}
	}

	private void runMenuOptionMapping(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [메뉴별 전용 옵션 설정 가이드] ---");
			System.out.println("팁: 특정 메뉴에만 적용할 특수 옵션(예: 에이드의 'ICE 전용')을 연결할 때 사용하세요.");
			System.out.println("\n[현재 등록된 전체 메뉴 목록]");
			adminController.listMenus();
			
			long menuId = readLong("\n설정할 메뉴의 [ID 번호]를 입력하세요 (취소: 0): ");
			if (menuId == 0) break;

			System.out.println("\n1. 새로운 옵션 그룹 연결 | 2. 기존 옵션 연결 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");
			if (sub == 1) {
				System.out.println("\n[연결 가능한 옵션 그룹 목록]");
				List<model.OptionGroup> allGroups = adminController.listOptionGroups();
				int groupIdx = readInt("\n연결할 옵션 그룹의 [목록 번호]를 입력하세요: ");
				if (groupIdx < 1 || groupIdx > allGroups.size()) {
					FailView.fail("잘못된 번호입니다.");
					continue;
				}
				int order = readInt("메뉴판 표시 순서 (예: 1): ");
				adminController.addOptionGroupToMenu(menuId, allGroups.get(groupIdx-1).getGroupId(), order);
			} else if (sub == 0) break;
		}
	}

	private void runMemberManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [회원 관리] ---");
			adminController.listMembers();
			System.out.println("\n1. 삭제 | 2. 포인트 수정 | 3. 등급 변경 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				long memberId = readLong("삭제할 회원 ID (취소: 0): ");
				if (memberId == 0)
					continue;
				adminController.deleteMember(memberId);
			} else if (sub == 2) {
				long memberId = readLong("포인트를 수정할 회원 ID (취소: 0): ");
				if (memberId == 0)
					continue;

				Member member = adminController.getMemberById(memberId);
				if (member == null) {
					FailView.fail("존재하지 않는 회원입니다.");
					continue;
				}

				if ("ADMIN".equalsIgnoreCase(member.getRole())) {
					FailView.fail("관리자 등급의 회원은 포인트를 수정할 수 없습니다.");
					continue;
				}

				System.out.println("지급할 금액은 양수(+), 차감할 금액은 음수(-)로 입력하세요.");
				int amount = readInt("수정할 포인트 금액: ");
				String reason = readText("변경 사유 (예: 이벤트 적립): ");
				adminController.updateMemberPoint(memberId, amount, reason);
			} else if (sub == 3) {
				long memberId = readLong("등급을 변경할 회원 ID (취소: 0): ");
				if (memberId == 0)
					continue;

				System.out.println("변경할 등급을 입력하세요 (ADMIN / USER)");
				String newRole = readText("새 등급: ");
				adminController.updateMemberRole(memberId, newRole);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runOrderManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [전체 주문 목록] ---");
			adminController.listOrders();
			System.out.println("\n1. 주문 취소(삭제) | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				long orderId = readLong("취소할 주문 ID: ");
				adminController.cancelOrder(orderId);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runStatisticsManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [매출 통계 및 분석 고도화] ---");
			System.out.println("1. 날짜별 매출 추이 (그래프)");
			System.out.println("2. 카테고리별 매출 분석 (비율)");
			System.out.println("3. 메뉴별 판매 순위 (Top 3)");
			System.out.println("4. 기간별 상세 조회 (객단가 분석)");
			System.out.println("5. 피크타임 통합 분석 (시간+요일)");
			System.out.println("6. 우수 회원 기여도 분석 (VVIP)");
			System.out.println("7. 전체 매출 통계 CSV로 내보내기");
			System.out.println("0. 뒤로");
			int choice = readInt("선택: ");

			switch (choice) {
			case 1:
				runDateStatistics(adminController);
				break;
			case 2:
				adminController.showCategoryStatistics();
				break;
			case 3:
				adminController.showMenuStatistics();
				break;
			case 4:
				runDetailedPeriodStatistics(adminController);
				break;
			case 5:
				adminController.showIntegratedPeakTimeStatistics();
				break;
			case 6:
				adminController.showTopMemberStatistics(5);
				break;
			case 7:
				adminController.exportStatistics();
				break;
			case 0:
				return;
			default:
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runDetailedPeriodStatistics(AdminController adminController) {
		System.out.println("\n--- [기간별 상세 조회] ---");
		String start = readText("시작일 (YYYY-MM-DD) (취소: 0): ");
		if (start.equals("0"))
			return;
		String end = readText("종료일 (YYYY-MM-DD): ");
		adminController.showDetailedPeriodStatistics(start, end);
	}

	private void runDateStatistics(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [날짜별 매출 추이] ---");
			System.out.println("1. 일별 매출 추이");
			System.out.println("2. 주차별 매출 추이");
			System.out.println("3. 월별 매출 추이");
			System.out.println("4. 연도별 매출 추이");
			System.out.println("0. 뒤로");
			int choice = readInt("선택: ");

			switch (choice) {
			case 1:
				adminController.showDateStatistics("일별 매출 추이", "%Y-%m-%d");
				break;
			case 2:
				adminController.showDateStatistics("주차별 매출 추이", "%Y-%u주");
				break;
			case 3:
				adminController.showDateStatistics("월별 매출 추이", "%Y-%m");
				break;
			case 4:
				adminController.showDateStatistics("연도별 매출 추이", "%Y");
				break;
			case 0:
				return;
			default:
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void printMainMenu() {
		System.out.println("\n[카페 키오스크 - New DB 모드]");
		System.out.println("1. 회원 로그인 및 주문 내역 조회");
		System.out.println("2. 비회원 주문");
		System.out.println("3. 관리자 모드 (카테고리/메뉴/통계)");
		System.out.println("0. 종료");
	}

	private int readInt(String prompt) {
		while (true) {
			String value = readText(prompt);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				FailView.fail("숫자를 입력해 주세요.");
			}
		}
	}

	private long readLong(String prompt) {
		while (true) {
			String value = readText(prompt);
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				FailView.fail("숫자를 입력해 주세요.");
			}
		}
	}

	private String readText(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine().trim();
	}
}
