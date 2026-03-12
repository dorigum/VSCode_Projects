package view;

import controller.AdminController;
import model.Member;
import repository.MenuRepository;
import service.AdminService;
import service.MemberService;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class MainView {
    public static void main(String[] args) {
        // [강제 설정] 콘솔 출력을 UTF-8로 고정
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MenuRepository menuRepository = new MenuRepository();
        AdminService adminService = new AdminService(menuRepository);
        AdminController adminController = new AdminController(adminService);
        MemberService memberService = new MemberService();

        Scanner scanner = new Scanner(System.in, "UTF-8");

        while (true) {
            System.out.println("\n[카페 키오스크 - New DB 모드]");
            System.out.println("1. 회원 로그인 및 주문 내역 조회");
            System.out.println("2. 비회원 주문 (준비 중)");
            System.out.println("3. 관리자 모드 (카테고리/메뉴/통계)");
            System.out.println("0. 종료");
            System.out.print("메뉴 선택: ");
            
            String input = scanner.nextLine();
            int mode;
            try {
                mode = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                continue;
            }

            if (mode == 1) {
                System.out.print("회원 번호 (ID): ");
                long memberId;
                try {
                    memberId = Long.parseLong(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("숫자로 된 회원 번호를 입력해 주세요.");
                    continue;
                }
                
                System.out.print("비밀번호: ");
                String password = scanner.nextLine();

                Member loggedInMember = memberService.login(memberId, password);
                if (loggedInMember != null) {
                    System.out.println("\n--- 내 정보 ---");
                    System.out.println(loggedInMember);
                    
                    System.out.println("\n1. 주문 내역 보기");
                    System.out.println("2. 로그아웃");
                    System.out.print("선택: ");
                    String subInput = scanner.nextLine();
                    if ("1".equals(subInput)) {
                        memberService.showOrderHistory(loggedInMember);
                    }
                }
            } else if (mode == 2) {
                System.out.println("준비 중인 서비스입니다.");
            } else if (mode == 3) {
                adminController.run();
            } else if (mode == 0) {
                System.out.println("프로그램을 종료합니다.");
                break;
            } else {
                System.out.println("잘못된 선택입니다.");
            }
        }
        scanner.close();
    }
}
