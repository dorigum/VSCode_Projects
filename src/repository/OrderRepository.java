package repository;

import model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    // 1. 총 매출액 조회
    public int getTotalSales() {
        String sql = "SELECT SUM(total_amount) FROM ORDERS WHERE status = 'COMPLETED'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. 카테고리별 매출 통계
    public Map<String, Integer> getSalesByCategory() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT category_name_snapshot, SUM(unit_price * quantity) as sales " +
                     "FROM ORDER_ITEM oi " +
                     "JOIN ORDERS o ON oi.order_id = o.order_id " +
                     "WHERE o.status = 'COMPLETED' " +
                     "GROUP BY category_name_snapshot " +
                     "ORDER BY sales DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("category_name_snapshot"), rs.getInt("sales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // 3. 인기 메뉴 Top 3 조회
    public List<String> getTopSellingMenus() {
        List<String> topMenus = new ArrayList<>();
        String sql = "SELECT menu_name_snapshot, SUM(quantity) as total_qty " +
                     "FROM ORDER_ITEM oi " +
                     "JOIN ORDERS o ON oi.order_id = o.order_id " +
                     "WHERE o.status = 'COMPLETED' " +
                     "GROUP BY menu_name_snapshot " +
                     "ORDER BY total_qty DESC LIMIT 3";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                topMenus.add(String.format("%-15s | %d개 판매", rs.getString("menu_name_snapshot"), rs.getInt("total_qty")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topMenus;
    }

    // 4. 일별 매출 데이터 추출 (최근 7일)
    public Map<String, Integer> getDailySales() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT DATE(order_date) as o_date, SUM(total_amount) as daily_total " +
                     "FROM ORDERS WHERE status = 'COMPLETED' " +
                     "GROUP BY DATE(order_date) " +
                     "ORDER BY o_date ASC LIMIT 7";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("o_date"), rs.getInt("daily_total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // 5. 주문 취소 (상태 업데이트)
    public boolean cancelOrder(long orderId) {
        String sql = "UPDATE ORDERS SET status = 'CANCELLED' WHERE order_id = ? AND status = 'COMPLETED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. 모든 주문 목록 조회 (취소용)
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS ORDER BY order_date DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new Order(
                    rs.getLong("order_id"),
                    rs.getLong("member_id"),
                    rs.getInt("total_amount"),
                    rs.getInt("point_used"),
                    rs.getInt("point_earned"),
                    rs.getString("status"),
                    rs.getTimestamp("order_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
