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

    public void addOptionGroupToCategory(int categoryId, long groupId, int displayOrder) {
        try {
            adminService.addOptionGroupToCategory(categoryId, groupId, displayOrder);
            EndView.success("카테고리에 옵션 그룹이 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void removeOptionGroupFromCategory(int categoryId, long groupId) {
        try {
            adminService.removeOptionGroupFromCategory(categoryId, groupId);
            EndView.success("카테고리에서 옵션 그룹이 삭제되었습니다.");
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

    public boolean registerMenu(int categoryId, String name, int price, String description) {
        try {
            adminService.registerMenu(categoryId, name, price, description);
            EndView.success("메뉴가 등록되었습니다.");
            return true;
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return false;
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

    // --- 옵션 관리 ---
    public List<model.OptionGroup> listOptionGroups() {
        try {
            List<model.OptionGroup> groups = adminService.getOptionGroupList();
            EndView.printOptionGroups(groups);
            return groups;
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return null;
        }
    }

    public void addOptionGroup(String name) {
        try {
            adminService.addOptionGroup(name);
            EndView.success("옵션 그룹이 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public List<model.MenuOption> listMenuOptions(model.OptionGroup group) {
        try {
            List<model.MenuOption> options = adminService.getMenuOptionsByGroup(group.getGroupId());
            EndView.printMenuOptions(group, options);
            return options;
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return null;
        }
    }

    public void addMenuOption(long groupId, String name, int extraPrice, int displayOrder) {
        try {
            adminService.addMenuOption(groupId, name, extraPrice, displayOrder);
            EndView.success("세부 옵션이 등록되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void updateMenuOption(long optionId, String name, int extraPrice, int displayOrder) {
        try {
            adminService.updateMenuOption(optionId, name, extraPrice, displayOrder);
            EndView.success("세부 옵션이 수정되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void deleteMenuOption(long optionId) {
        try {
            adminService.deleteMenuOption(optionId);
            EndView.success("세부 옵션이 삭제되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    // --- 통계 ---
    public void showStatistics() {
        showDateStatistics("일별 매출 추이", "%Y-%m-%d");
    }

    public void showDateStatistics(String periodTitle, String format) {
        try {
            int totalSales = adminService.getTotalSales();
            Map<String, Integer> periodSales = adminService.getSalesByPeriod(format);

            if ("%Y-%u주".equals(format)) {
                periodSales = formatWeekData(periodSales);
            }

            EndView.printDateSalesReport(periodTitle, totalSales, periodSales);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showCategoryStatistics() {
        try {
            Map<String, Integer> categorySales = adminService.getSalesByCategory();
            EndView.printCategorySalesReport(categorySales);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showMenuStatistics() {
        try {
            List<String> topMenus = adminService.getTopSellingMenus();
            EndView.printMenuSalesReport(topMenus);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    private Map<String, Integer> formatWeekData(Map<String, Integer> periodSales) {
        Map<String, Integer> formattedSales = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : periodSales.entrySet()) {
            String key = entry.getKey();
            try {
                String[] parts = key.replace("주", "").split("-");
                int year = Integer.parseInt(parts[0]);
                int week = Integer.parseInt(parts[1]);

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.YEAR, year);
                cal.set(java.util.Calendar.WEEK_OF_YEAR, week);
                cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);

                int month = cal.get(java.util.Calendar.MONTH) + 1;
                int weekOfMonth = cal.get(java.util.Calendar.WEEK_OF_MONTH);

                String newKey = String.format("%d-%d월 %d주", year, month, weekOfMonth);
                formattedSales.put(newKey, entry.getValue());
            } catch (Exception e) {
                formattedSales.put(key, entry.getValue());
            }
        }
        return formattedSales;
    }
}