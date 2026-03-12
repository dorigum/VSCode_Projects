package controller;

import model.Member;
import model.Menu;
import model.Category;
import model.Order;
import service.AdminService;
import java.util.Scanner;
import java.util.List;

public class AdminController {
    private AdminService adminService;
    private Scanner scanner;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
        this.scanner = new Scanner(System.in, "UTF-8");
    }

    public void run() {
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
                continue;
            }

            switch (choice) {
                case 1: categoryManagement(); break;
                case 2: menuManagement(); break;
                case 3: memberManagement(); break;
                case 4: orderManagement(); break;
                case 5: adminService.showStatistics(); break;
                case 0: return;
                default: System.out.println("잘못된 선택입니다.");
            }
        }
    }

    // --- 주문 관리 ---
    private void orderManagement() {
        while (true) {
            System.out.println("\n--- [전체 주문 목록] ---");
            List<Order> orders = adminService.getOrderList();
            orders.forEach(System.out::println);
            System.out.println("1. 주문 취소 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("취소할 주문 ID: ");
                try {
                    adminService.cancelOrder(Long.parseLong(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) break;
        }
    }

    // --- 카테고리 관리 ---
    private void categoryManagement() {
        while (true) {
            System.out.println("\n--- [카테고리 관리] ---");
            adminService.getCategoryList().forEach(System.out::println);
            System.out.println("1. 추가 | 2. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("새 카테고리명: ");
                adminService.addCategory(scanner.nextLine());
            } else if ("2".equals(sub)) {
                System.out.print("삭제할 카테고리 ID: ");
                try {
                    adminService.deleteCategory(Integer.parseInt(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) break;
        }
    }

    // --- 메뉴 관리 ---
    private void menuManagement() {
        while (true) {
            System.out.println("\n--- [메뉴 관리] ---");
            List<Menu> menus = adminService.getMenuList();
            menus.forEach(System.out::println);
            System.out.println("1. 등록 | 2. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.println("\n[현재 카테고리 목록]");
                adminService.getCategoryList().forEach(System.out::println);
                System.out.print("카테고리 ID: ");
                int catId = Integer.parseInt(scanner.nextLine());
                System.out.print("메뉴명: ");
                String name = scanner.nextLine();
                System.out.print("가격: ");
                int price = Integer.parseInt(scanner.nextLine());
                System.out.print("설명: ");
                String desc = scanner.nextLine();
                adminService.registerMenu(catId, name, price, desc);
            } else if ("2".equals(sub)) {
                System.out.print("삭제할 메뉴 ID: ");
                try {
                    adminService.deleteMenu(Long.parseLong(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) break;
        }
    }

    // --- 회원 관리 ---
    private void memberManagement() {
        while (true) {
            System.out.println("\n--- [회원 관리] ---");
            List<Member> members = adminService.getMemberList();
            members.forEach(m -> System.out.println(m));
            System.out.println("1. 삭제 | 0. 뒤로");
            System.out.print("선택: ");
            String sub = scanner.nextLine();

            if ("1".equals(sub)) {
                System.out.print("삭제할 회원 ID: ");
                try {
                    adminService.deleteMember(Long.parseLong(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력해 주세요.");
                }
            } else if ("0".equals(sub)) break;
        }
    }
}
