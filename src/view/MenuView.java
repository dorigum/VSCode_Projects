package view;

import controller.AdminController;
import controller.MemberController;
import model.Member;

import java.util.Scanner;

public class MenuView {
    private static final Scanner scanner = new Scanner(System.in, "UTF-8");

    public void run(AdminController adminController, MemberController memberController) {
        while (true) {
            printMainMenu();
            int choice = readInt("메뉴 선택: ");

            if (choice == 1) {
                runMemberFlow(memberController);
            } else if (choice == 2) {
                EndView.success("준비 중인 서비스입니다.");
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
        String phone = readText("휴대폰 번호 (Phone): ");
        String password = readText("비밀번호: ");
        Member member = memberController.login(phone, password);

        if (member == null) {
            return;
        }

        while (true) {
            System.out.println("\n1. 주문 내역 보기");
            System.out.println("2. 로그아웃");
            int sub = readInt("선택: ");

            if (sub == 1) {
                memberController.showOrderHistory(member);
            } else if (sub == 2) {
                break;
            } else {
                FailView.fail("잘못된 선택입니다.");
            }
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
