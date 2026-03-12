package controller;

import exception.CafeKioskException;
import exception.ValidationException;
import model.Category;
import model.Member;
import model.Menu;
import model.Order;
import service.AdminService;
import view.EndView;
import view.FailView;

import java.util.List;
import java.util.Map;

public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        if (adminService == null) {
            throw new ValidationException("AdminService는 null일 수 없습니다.");
        }
        this.adminService = adminService;
    }

    public void listCategories() {
        try {
            List<Category> categories = adminService.getCategoryList();
            EndView.printCategories(categories);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void addCategory(String name) {
        try {
            adminService.addCategory(name);
            EndView.success("카테고리가 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void deleteCategory(int id) {
        try {
            adminService.deleteCategory(id);
            EndView.success("카테고리가 삭제되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void listMenus() {
        try {
            List<Menu> menus = adminService.getMenuList();
            EndView.printMenus(menus);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void registerMenu(int categoryId, String name, int price, String description) {
        try {
            adminService.registerMenu(categoryId, name, price, description);
            EndView.success("메뉴가 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void deleteMenu(long id) {
        try {
            adminService.deleteMenu(id);
            EndView.success("메뉴가 삭제되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void listMembers() {
        try {
            List<Member> members = adminService.getMemberList();
            EndView.printMembers(members);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void deleteMember(long id) {
        try {
            adminService.deleteMember(id);
            EndView.success("회원을 삭제했습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void listOrders() {
        try {
            List<Order> orders = adminService.getOrderList();
            EndView.printOrders(orders);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void cancelOrder(long orderId) {
        try {
            adminService.cancelOrder(orderId);
            EndView.success("주문이 취소되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showStatistics() {
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
