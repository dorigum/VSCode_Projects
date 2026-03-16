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

    public Category getCategoryById(int id) {
        try {
            return adminService.getCategoryById(id);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return null;
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
            EndView.printMenu(menus);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public List<Menu> getMenuList() {
        try {
            return adminService.getMenuList();
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return null;
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

    public void updateMenu(long menuId, int categoryId, String name, int price, String description, boolean isAvailable) {
        try {
            adminService.updateMenu(menuId, categoryId, name, price, description, isAvailable);
            EndView.success("메뉴 정보가 수정되었습니다.");
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

    public void addOptionGroupToMenu(long menuId, long groupId, int displayOrder) {
        try {
            // AdminService에 해당 메서드가 이미 있다고 가정 (기존에 메뉴 등록 시 사용됨)
            // 확인 결과 AdminService 인터페이스에는 없으므로 추가 필요할 수 있음
            // 하지만 AdminServiceImpl에서는 menuRepository.addOptionGroupToMenu를 직접 호출할 수 있으므로
            // 여기서는 편의상 repository를 직접 사용하거나 service에 위임하는 메서드를 추가합니다.
            adminService.addOptionGroupToMenu(menuId, groupId, displayOrder);
            EndView.success("메뉴에 옵션 그룹이 성공적으로 연결되었습니다.");
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

    public Member getMemberById(long id) {
        try {
            List<Member> members = adminService.getMemberList();
            return members.stream().filter(m -> m.getMemberId() == id).findFirst().orElse(null);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
            return null;
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

    public void updateMemberRole(long id, String newRole) {
        try {
            adminService.updateMemberRole(id, newRole);
            EndView.success("회원 등급이 변경되었습니다.");
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void updateMemberPoint(long id, int amount, String reason) {
        try {
            adminService.updateMemberPoint(id, amount, reason);
            String sign = amount > 0 ? "+" : "";
            EndView.success(String.format("회원 포인트가 수정되었습니다. (%s%d원)\n사유: %s", sign, amount, reason));
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

    public void deleteOptionGroup(long groupId) {
        try {
            adminService.deleteOptionGroup(groupId);
            EndView.success("옵션 그룹이 삭제되었습니다.");
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

    public void showDetailedPeriodStatistics(String startDate, String endDate) {
        try {
            Map<String, Object> stats = adminService.getSalesStatsByPeriod(startDate, endDate);
            EndView.printDetailedPeriodReport(startDate, endDate, stats);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showHourlySalesStatistics() {
        try {
            Map<Integer, Integer> hourlySales = adminService.getHourlySales();
            EndView.printHourlySalesReport(hourlySales);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showDayOfWeekStatistics() {
        try {
            Map<String, Integer> daySales = adminService.getDayOfWeekSales();
            EndView.printDayOfWeekSalesReport(daySales);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showIntegratedPeakTimeStatistics() {
        try {
            System.out.println("\n[📊 통합 피크타임 매출 분석 리포트]");
            Map<Integer, Integer> hourlySales = adminService.getHourlySales();
            Map<String, Integer> daySales = adminService.getDayOfWeekSales();
            
            EndView.printHourlySalesReport(hourlySales);
            EndView.printDayOfWeekSalesReport(daySales);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void showTopMemberStatistics(int limit) {
        try {
            List<Map<String, Object>> topMembers = adminService.getTopSpenders(limit);
            EndView.printTopMemberReport(topMembers);
        } catch (CafeKioskException e) {
            FailView.fail(e.getMessage());
        }
    }

    public void exportStatistics() {
        try {
            adminService.exportStatisticsToCSV();
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