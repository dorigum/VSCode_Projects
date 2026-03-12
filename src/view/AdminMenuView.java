package view;

import controller.AdminController;
import java.util.Scanner;

public class AdminMenuView {
    public static void adminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== [관리자 통합 관리 모드] =====");
            System.out.println("1. 카테고리 관리 (CRUD)");
            System.out.println("2. 메뉴 관리 (CRUD)");
            System.out.println("3. 회원 관리 (조회/삭제)");
            System.out.println("4. 주문 관리 (취소)");
            System.out.println("5. 매출 통계 및 그래프");
            System.out.println("0. 메인 메뉴로 돌아가기");
            System.out.print("선택: ");
            
            String choiceStr = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해 주세요.");
                continue;
            }

            switch (choice) {
                case 1: categoryManagement(scanner); break;
                case 2: menuManagement(scanner); break;
                case 3: memberManagement(scanner); break;
                case 4: orderManagement(scanner); break;
                case 5: AdminController.showStatistics(); break;
                case 0: return;
                default: System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private static void categoryManagement(Scanner scanner) {
        while (true) {
            System.out.println("\n--- [카테고리 관리] ---");
            AdminController.getCategoryList();

            System.out.println("\n1. 추가 | 2. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("새 카테고리명: ");
                String name = scanner.nextLine();
                if (name != null && !name.trim().isEmpty()) {
                    AdminController.addCategory(name.trim());
                } else {
                    System.out.println("카테고리 이름을 입력해 주세요.");
                }
            } else if ("2".equals(sub)) {
                System.out.print("삭제할 카테고리 ID: ");
                try {
                    int catId = Integer.parseInt(scanner.nextLine());
                    AdminController.deleteCategory(catId);
                } catch (NumberFormatException e) {
                    System.out.println("올바른 ID(숫자)를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) {
                break;
            }
        }
    }

    private static void menuManagement(Scanner scanner) {
        while (true) {
            System.out.println("\n--- [메뉴 관리] ---");
            AdminController.getMenuList();

            System.out.println("\n1. 등록 | 2. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                try {
                    System.out.println("\n[현재 카테고리 목록]");
                    AdminController.getCategoryList();
                    
                    System.out.print("카테고리 ID: ");
                    int catId = Integer.parseInt(scanner.nextLine());
                    System.out.print("메뉴명: ");
                    String name = scanner.nextLine();
                    System.out.print("가격: ");
                    int price = Integer.parseInt(scanner.nextLine());
                    System.out.print("설명: ");
                    String desc = scanner.nextLine();
                    
                    AdminController.registerMenu(catId, name, price, desc);
                } catch (NumberFormatException e) {
                    System.out.println("잘못된 숫자 형식입니다. 다시 입력해 주세요.");
                }
            } else if ("2".equals(sub)) {
                System.out.print("삭제할 메뉴 ID: ");
                try {
                    long menuId = Long.parseLong(scanner.nextLine());
                    AdminController.deleteMenu(menuId);
                } catch (NumberFormatException e) {
                    System.out.println("올바른 ID(숫자)를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) {
                break;
            }
        }
    }

    private static void memberManagement(Scanner scanner) {
        while (true) {
            System.out.println("\n--- [회원 관리] ---");
            AdminController.getMemberList();

            System.out.println("\n1. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("삭제할 회원 ID: ");
                try {
                    long memberId = Long.parseLong(scanner.nextLine());
                    AdminController.deleteMember(memberId);
                } catch (NumberFormatException e) {
                    System.out.println("올바른 ID(숫자)를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) {
                break;
            }
        }
    }

    private static void orderManagement(Scanner scanner) {
        while (true) {
            System.out.println("\n--- [전체 주문 목록] ---");
            AdminController.getOrderList();
            
            System.out.println("\n1. 주문 취소 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("취소할 주문 ID: ");
                try {
                    long orderId = Long.parseLong(scanner.nextLine());
                    AdminController.cancelOrder(orderId);
                } catch (NumberFormatException e) {
                    System.out.println("올바른 주문 ID(숫자)를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) {
                break;
            }
        }
    }
}
