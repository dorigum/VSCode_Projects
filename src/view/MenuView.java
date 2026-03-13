package view;

import controller.AdminController;
import controller.MemberController;
import controller.MenuController;
import model.Member;
import java.util.Scanner;
import java.util.List;

public class MenuView {
	private static final Scanner scanner = new Scanner(System.in, "UTF-8");

	public void run(AdminController adminController, MemberController memberController,
			MenuController menuController) {
		while (true) {
			printMainMenu();
			int choice = readInt("메뉴 선택: ");

			if (choice == 1) {
				runMemberFlow(memberController, menuController);
			} else if (choice == 2) {
				new OrderingView(scanner).run(menuController, null); // 비회원 주문
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

		// 회원가입 or 로그인 선택
		System.out.println("\n1. 로그인");
		System.out.println("2. 회원가입");
		System.out.println("0. 뒤로");
		int choice = readInt("선택: ");

		if (choice == 2) {
			// 회원가입
			String phone = readText("전화번호 (010-1234-5678): ");
			String password = readText("비밀번호 (4자리 이상): ");
			int age = readInt("나이: ");
			memberController.register(phone, password, age);
			return;
		} else if (choice == 0) {
			return;
		}

		// 로그인
		String phone = readText("휴대폰 번호 (Phone): ");
		String password = readText("비밀번호: ");
		Member member = memberController.login(phone, password);

		if (member == null)
			return;

		// 로그인 후 메뉴
		while (true) {
			System.out.println("\n1. 주문 내역 보기");
			System.out.println("2. 찜 목록 보기");
			System.out.println("3. 퀵오더 (최근 주문 바로 주문)");
			System.out.println("4. 주문하기 (메뉴판 보기)");
			System.out.println("0. 로그아웃");
			int sub = readInt("선택: ");

			if (sub == 1) {
				memberController.showOrderHistory(member);
			} else if (sub == 2) {
				memberController.showWishlist(member);
			} else if (sub == 3) {
				memberController.showQuickOrder(member);
			} else if (sub == 4) {
				new OrderingView(scanner).run(menuController, member);
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
		
		if (admin == null) return;
		
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
			System.out.println("\n1. 그룹 추가 | 2. 세부 옵션 관리 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				String name = readText("새 옵션 그룹명 (예: 온도, 사이즈): ");
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
				int optIdx = readInt("수정할 옵션 번호: ");
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
				int optIdx = readInt("삭제할 옵션 번호: ");
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
				int id = readInt("삭제할 카테고리 ID: ");
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
			System.out.println("\n--- [메뉴 관리] ---");
			adminController.listMenus();
			System.out.println("\n1. 등록 | 2. 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				while (true) {
					System.out.println("\n[현재 카테고리 목록]");
					adminController.listCategories();

					int categoryId = readInt("카테고리 ID (취소: 0): ");
					if (categoryId == 0) break;
					
					String name = readText("메뉴명: ");
					int price = readInt("가격: ");
					String description = readText("설명: ");
					
					if (adminController.registerMenu(categoryId, name, price, description)) {
						break;
					}
				}
			} else if (sub == 2) {
				long menuId = readLong("삭제할 메뉴 ID: ");
				adminController.deleteMenu(menuId);
			} else if (sub == 0) {
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runMemberManagement(AdminController adminController) {
		while (true) {
			System.out.println("\n--- [회원 관리] ---");
			adminController.listMembers();
			System.out.println("\n1. 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				long memberId = readLong("삭제할 회원 ID: ");
				adminController.deleteMember(memberId);
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
			System.out.println("\n--- [매출 통계 및 그래프 조회] ---");
			System.out.println("1. 날짜별 매출 추이");
			System.out.println("2. 카테고리별 매출 추이");
			System.out.println("3. 메뉴별 매출 추이");
			System.out.println("0. 뒤로");
			int choice = readInt("선택: ");

			switch (choice) {
				case 1: runDateStatistics(adminController); break;
				case 2: adminController.showCategoryStatistics(); break;
				case 3: adminController.showMenuStatistics(); break;
				case 0: return;
				default: FailView.fail("잘못된 선택입니다.");
			}
		}
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
				case 1: adminController.showDateStatistics("일별 매출 추이", "%Y-%m-%d"); break;
				case 2: adminController.showDateStatistics("주차별 매출 추이", "%Y-%u주"); break;
				case 3: adminController.showDateStatistics("월별 매출 추이", "%Y-%m"); break;
				case 4: adminController.showDateStatistics("연도별 매출 추이", "%Y"); break;
				case 0: return;
				default: FailView.fail("잘못된 선택입니다.");
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
