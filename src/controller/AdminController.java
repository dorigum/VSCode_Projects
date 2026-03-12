package controller;

import java.util.List;
import model.Category;
import model.Member;
import model.Menu;
import model.Order;
import repository.MenuRepositoryImpl;
import service.AdminService;
import service.AdminServiceImpl;
import view.EndView;
import view.FailView;

public class AdminController {
    private static AdminService adminService = new AdminServiceImpl(new MenuRepositoryImpl());

    // --- 카테고리 관리 ---
    public static void getCategoryList() {
        try {
            List<Category> categories = adminService.getCategoryList();
            EndView.printCategoryList(categories);
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void addCategory(String name) {
        try {
            adminService.addCategory(name);
            EndView.printMessage("카테고리가 추가되었습니다.");
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void deleteCategory(int id) {
        try {
            adminService.deleteCategory(id);
            EndView.printMessage("카테고리가 삭제되었습니다.");
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    // --- 메뉴 관리 ---
    public static void getMenuList() {
        try {
            List<Menu> menus = adminService.getMenuList();
            EndView.printMenuList(menus);
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void registerMenu(int categoryId, String name, int price, String description) {
        try {
            adminService.registerMenu(categoryId, name, price, description);
            EndView.printMessage("메뉴가 등록되었습니다.");
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void deleteMenu(long id) {
        try {
            adminService.deleteMenu(id);
            EndView.printMessage("메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    // --- 회원 관리 ---
    public static void getMemberList() {
        try {
            List<Member> members = adminService.getMemberList();
            EndView.printMemberList(members);
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void deleteMember(long id) {
        try {
            adminService.deleteMember(id);
            EndView.printMessage("회원이 삭제되었습니다.");
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    // --- 주문 관리 ---
    public static void getOrderList() {
        try {
            List<Order> orders = adminService.getOrderList();
            EndView.printOrderList(orders);
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    public static void cancelOrder(long orderId) {
        try {
            if (adminService.cancelOrder(orderId)) {
                EndView.printMessage("주문이 취소되었습니다.");
            } else {
                FailView.errorMessage("취소 실패: 존재하지 않는 주문이거나 이미 취소된 주문입니다.");
            }
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }

    // --- 통계 ---
    public static void showStatistics() {
        try {
            adminService.showStatistics();
        } catch (Exception e) {
            FailView.errorMessage(e.getMessage());
        }
    }
}
