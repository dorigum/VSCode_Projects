package controller;

import exception.CafeKioskException;
import model.Category;
import model.Member;
import model.Menu;
import model.Order;
import service.AdminService;
import service.AdminServiceImpl;
import view.EndView;
import view.FailView;

import java.util.List;
import java.util.Map;

public class AdminController {
    private static AdminService adminService = new AdminServiceImpl();

    // --- 카테고리 관리 ---
    public static void getCategoryList() {
        try {
            List<Category> categories = adminService.getCategoryList();
            EndView.printCategories(categories);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void addCategory(String name) {
        try {
            adminService.addCategory(name);
            EndView.success("카테고리가 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void deleteCategory(int id) {
        try {
            adminService.deleteCategory(id);
            EndView.success("카테고리가 삭제되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    // --- 메뉴 관리 ---
    public static void getMenuList() {
        try {
            List<Menu> menus = adminService.getMenuList();
            EndView.printMenus(menus);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void registerMenu(int categoryId, String name, int price, String description) {
        try {
            adminService.registerMenu(categoryId, name, price, description);
            EndView.success("메뉴가 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void deleteMenu(long id) {
        try {
            adminService.deleteMenu(id);
            EndView.success("메뉴가 삭제되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    // --- 회원 관리 ---
    public static void getMemberList() {
        try {
            List<Member> members = adminService.getMemberList();
            EndView.printMembers(members);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void deleteMember(long id) {
        try {
            adminService.deleteMember(id);
            EndView.success("회원을 삭제했습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    // --- 주문 관리 ---
    public static void getOrderList() {
        try {
            List<Order> orders = adminService.getOrderList();
            EndView.printOrders(orders);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public static void cancelOrder(long orderId) {
        try {
            adminService.cancelOrder(orderId);
            EndView.success("주문이 취소되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    // --- 통계 ---
    public static void showStatistics() {
        try {
            int totalSales = adminService.getTotalSales();
            Map<String, Integer> dailySales = adminService.getDailySales();
            Map<String, Integer> categorySales = adminService.getSalesByCategory();
            List<String> topMenus = adminService.getTopSellingMenus();
            EndView.printSalesReport(totalSales, dailySales, categorySales, topMenus);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }
}
