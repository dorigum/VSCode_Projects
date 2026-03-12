package view;

import controller.AdminController;
import controller.MemberController;
import controller.MenuController;
import model.Member;
import model.Menu;
import model.OrderItem;
import model.OptionGroup;
import model.Option;
import java.util.Scanner;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import java.util.ArrayList;
import java.util.List;

public class MenuView {
	private static final Scanner scanner = new Scanner(System.in, "UTF-8");

	public void run(AdminController adminController, MemberController memberController,
			MenuController menuController) {
		while (true) {
			printMainMenu();
			int choice = readInt("메뉴 선택: ");

			if (choice == 1) {
				runMemberFlow(memberController);
			} else if (choice == 2) {
				runMenuFlow(menuController, null); // 비회원 주문
			} else if (choice == 3) {
				runAdminFlow(adminController);
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

	private void runMemberFlow(MemberController memberController) {

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
			System.out.println("0. 로그아웃");
			int sub = readInt("선택: ");

			if (sub == 1) {
				memberController.showOrderHistory(member);
			} else if (sub == 2) {
				memberController.showWishlist(member);
			} else if (sub == 3) {
				memberController.showQuickOrder(member);
			} else if (sub == 0) {
				EndView.success("로그아웃 되었습니다.");
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
		}
	}

	private void runMenuFlow(MenuController menuController, Member member) {
		List<OrderItem> cart = new ArrayList<>();
		while (true) {
			System.out.println("\n1. 인기 상품");
			System.out.println("2. 신상품");
			System.out.println("3. 커피");
			System.out.println("4. 논커피");
			System.out.println("5. 디저트");
			System.out.println("8. 카트확인");
			System.out.println("9. 주문하기");
			System.out.println("0. 뒤로가기");
			// System.out.println("4. 로그아웃");
			int sub = readInt("선택: ");

			List<Menu> menus = null;
			if (sub == 1) {
				menus = menuController.getPopularMenuList();
			} else if (sub == 2) {
				menus = menuController.getLatestMenuList();
			} else if (sub == 3) {
				menus = menuController.getCoffeeMenuList();
			} else if (sub == 4) {
				menus = menuController.getNonCoffeeMenuList();
			} else if (sub == 5) {
				menus = menuController.getDesertMenuList();
			} else if (sub == 8) {
				EndView.printCart(cart);
			} else if (sub == 9) {
				runOrderFlow(menuController, cart, member);
				// 주문 완료
			} else if (sub == 0) {
				EndView.success("뒤로 돌아갑니다..");
				break;
			} else {
				FailView.fail("잘못된 선택입니다.");
			}
			if (menus != null) {
				// 메뉴 조회 성공시 메뉴 선택
				cart = runMenuSelectFlow(menuController, menus, cart, member);
			}
		}
	}

	private List<OrderItem> runMenuSelectFlow(MenuController menuController, List<Menu> menus,
			List<OrderItem> cart, Member member) {
		// 메뉴에서 카트에 담아 카트를 반환.
		EndView.printMenu(menus);
		while (true) {
			int menuChoice = readInt("메뉴 선택 (0. 뒤로): ");
			if (menuChoice == 0) {
				// 메뉴 목록 선택으로 이동
				break;
			}
			Menu selectedMenu = menus.get(menuChoice - 1);
			// 옵션선택 > 메뉴와 관련된 옵션 그룹 목록을 보여줘 차례로 선택하게함
			List<OptionGroup> optionGroups = menuController.getOptionGroups(selectedMenu);
			
			// 옵션 선택
			List<Option> selectedOptions = new ArrayList<>();
			boolean optionSelectCancled = false;
			for (OptionGroup optionGroup : optionGroups) {
				EndView.printOptionGroup(optionGroup);
				
				List<Option> options = menuController.getOptions(optionGroup);

				int optionChoice = readInt("옵션 선택 (0. 뒤로): ");
				if (optionChoice == 0) {
					optionSelectCancled = true;
					break;
				}
				Option selectedOption = options.get(optionChoice - 1);
				selectedOptions.add(selectedOption);				
				System.out.println("옵션 선택 완료");				
			}
			if (optionSelectCancled) {
				break;
			}
			int quantity = readInt("개수 선택 (0. 뒤로): ");

			String categorySnapshot = menuController.getCategoryName(selectedMenu);
			cart.add(new OrderItem(0, 0, selectedMenu.getMenuId(), quantity, selectedMenu.getPrice(), selectedMenu.getMenuName(), categorySnapshot, selectedOptions));
		}
		return cart;
	}

	private void runOrderFlow(MenuController menuController, List<OrderItem> cart, Member member) {
		while (true) {
			// 주문시 카트 내용 보여주기
			EndView.printCart(cart);
			int sub = readInt("주문하시겠습니까? 1. 주문, 0. 뒤로): ");
			if (sub == 0) {
				EndView.success("뒤로 돌아갑니다..");
				break;
			}			
			int result = menuController.order(cart, null); // 비회원주문
			if (result == 1) {
				EndView.success("주문이 완료되었습니다.");
			} else {
				FailView.fail("주문에 실패했습니다.");
			}
			break;



			// if (sub == 1) {
			// List<Menu> popularMenus = menuController.getPopularMenuList();
			// } else if (sub == 2) {
			// List<Menu> latestMenus = menuController.getLatestMenuList();
			// } else if (sub == 3) {
			// List<Menu> coffeeMenus = menuController.getCoffeeMenuList();
			// } else if (sub == 4) {
			// List<Menu> nonCoffeeMenus = menuController.getNonCoffeeMenuList();
			// } else if (sub == 5) {
			// List<Menu> desertMenus = menuController.getDesertMenuList();
			// } else if (sub == 0) {
			// EndView.success("뒤로 돌아갑니다..");
			// break;
			// } else {
			// FailView.fail("잘못된 선택입니다.");
			// }
		}
	}



	private void runAdminFlow(AdminController adminController) {
		while (true) {
			System.out.println("\n===== [관리자 통합 관리 모드] =====");
			System.out.println("1. 카테고리 관리 (CRUD)");
			System.out.println("2. 메뉴 관리 (CRUD)");
			System.out.println("3. 회원 관리 (조회/삭제)");
			System.out.println("4. 주문 관리 (취소)");
			System.out.println("5. 매출 통계 및 그래프");
			System.out.println("0. 메인 메뉴로 돌아가기");

			int choice = readInt("선택: ");

			if (choice == 1) {
				runCategoryManagement(adminController);
			} else if (choice == 2) {
				runMenuManagement(adminController);
			} else if (choice == 3) {
				runMemberManagement(adminController);
			} else if (choice == 4) {
				runOrderManagement(adminController);
			} else if (choice == 5) {
				adminController.showStatistics();
			} else if (choice == 0) {
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
		List<OrderItem> cart = new ArrayList<>();
		while (true) {
			System.out.println("\n--- [메뉴 관리] ---");
			adminController.listMenus();
			System.out.println("\n1. 등록 | 2. 삭제 | 0. 뒤로");
			int sub = readInt("선택: ");

			if (sub == 1) {
				System.out.println("\n[현재 카테고리 목록]");
				adminController.listCategories();

				int categoryId = readInt("카테고리 ID: ");
				String name = readText("메뉴명: ");
				int price = readInt("가격: ");
				String description = readText("설명: ");
				adminController.registerMenu(categoryId, name, price, description);
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

			System.out.println("\n1. 주문 취소 | 0. 뒤로");
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

	private void printMainMenu() {
		System.out.println("\n[카페 키오스크 - New DB 모드]");
		System.out.println("1. 회원 로그인 및 주문 내역 조회");
		System.out.println("2. 비회원 주문 (준비 중)");
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
