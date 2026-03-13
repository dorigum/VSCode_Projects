package repository;

import exception.RepositoryException;
import model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderRepositoryImpl implements OrderRepository {

    public int getTotalSales() {
        String sql = "SELECT SUM(total_amount) FROM ORDERS WHERE status = 'COMPLETED'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RepositoryException("총 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    public Map<String, Integer> getSalesByCategory() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT category_name_snapshot, SUM(unit_price * quantity) as sales "
                     + "FROM ORDER_ITEM oi "
                     + "JOIN ORDERS o ON oi.order_id = o.order_id "
                     + "WHERE o.status = 'COMPLETED' "
                     + "GROUP BY category_name_snapshot "
                     + "ORDER BY sales DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("category_name_snapshot"), rs.getInt("sales"));
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리별 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    public List<String> getTopSellingMenus() {
        List<String> topMenus = new ArrayList<>();
        String sql = "SELECT menu_name_snapshot, SUM(quantity) as total_qty "
                     + "FROM ORDER_ITEM oi "
                     + "JOIN ORDERS o ON oi.order_id = o.order_id "
                     + "WHERE o.status = 'COMPLETED' "
                     + "GROUP BY menu_name_snapshot "
                     + "ORDER BY total_qty DESC LIMIT 3";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                topMenus.add(String.format("%-15s | %d개 판매", rs.getString("menu_name_snapshot"), rs.getInt("total_qty")));
            }
            return topMenus;
        } catch (SQLException e) {
            throw new RepositoryException("인기 메뉴 조회 중 오류가 발생했습니다.", e);
        }
    }

    public Map<String, Integer> getDailySales() {
        return getSalesByPeriod("%Y-%m-%d");
    }

    public Map<String, Integer> getSalesByPeriod(String format) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(order_date, ?) as period, SUM(total_amount) as total "
                     + "FROM ORDERS WHERE status = 'COMPLETED' "
                     + "GROUP BY period "
                     + "ORDER BY period ASC LIMIT 10";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, format);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("period"), rs.getInt("total"));
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("기간별 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Map<String, Object> getSalesStatsByPeriod(String startDate, String endDate) {
        Map<String, Object> stats = new LinkedHashMap<>();
        String sql = "SELECT COUNT(*) as order_count, SUM(total_amount) as total_amount "
                     + "FROM ORDERS "
                     + "WHERE order_date BETWEEN ? AND ? AND status = 'COMPLETED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate + " 00:00:00");
            pstmt.setString(2, endDate + " 23:59:59");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("count", rs.getInt("order_count"));
                    stats.put("amount", rs.getInt("total_amount"));
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("기간별 상세 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Map<Integer, Integer> getHourlySales() {
        Map<Integer, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT HOUR(order_date) as hour, SUM(total_amount) as total "
                     + "FROM ORDERS WHERE status = 'COMPLETED' "
                     + "GROUP BY hour ORDER BY hour";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getInt("hour"), rs.getInt("total"));
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("시간대별 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<Map<String, Object>> getTopSpenders(int limit) {
        List<Map<String, Object>> spenders = new ArrayList<>();
        String sql = "SELECT m.phone, SUM(o.total_amount) as total_spent "
                     + "FROM ORDERS o "
                     + "JOIN MEMBER m ON o.member_id = m.member_id "
                     + "WHERE o.status = 'COMPLETED' "
                     + "GROUP BY m.member_id "
                     + "ORDER BY total_spent DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("phone", rs.getString("phone"));
                    map.put("total", rs.getInt("total_spent"));
                    spenders.add(map);
                }
            }
            return spenders;
        } catch (SQLException e) {
            throw new RepositoryException("회원별 기여도 조회 중 오류가 발생했습니다.", e);
        }
    }

    public boolean cancelOrder(long orderId) {
        String selectSql = "SELECT member_id, point_used, point_earned, status FROM ORDERS WHERE order_id = ?";
        String updateOrderSql = "UPDATE ORDERS SET status = 'CANCELLED' WHERE order_id = ?";
        String updateMemberSql = "UPDATE MEMBER SET point_balance = point_balance + ? - ? WHERE member_id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            long memberId = 0;
            int pointUsed = 0;
            int pointEarned = 0;
            String currentStatus = "";

            // 1. 주문 정보 조회
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setLong(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        memberId = rs.getLong("member_id");
                        pointUsed = rs.getInt("point_used");
                        pointEarned = rs.getInt("point_earned");
                        currentStatus = rs.getString("status");
                    } else {
                        return false;
                    }
                }
            }

            // 이미 취소된 주문이면 종료
            if ("CANCELLED".equals(currentStatus)) {
                conn.rollback();
                return false;
            }

            // 2. 주문 상태를 CANCELLED로 변경
            try (PreparedStatement pstmt = conn.prepareStatement(updateOrderSql)) {
                pstmt.setLong(1, orderId);
                pstmt.executeUpdate();
            }

            // 3. 회원인 경우 포인트 복구/회수 로직 실행
            if (memberId > 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(updateMemberSql)) {
                    pstmt.setInt(1, pointUsed);   // 사용했던 포인트 다시 더해줌
                    pstmt.setInt(2, pointEarned); // 적립됐던 포인트는 다시 빼줌
                    pstmt.setLong(3, memberId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // 모든 작업 성공 시 커밋
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { }
            }
            throw new RepositoryException("주문 취소 및 포인트 복구 중 오류가 발생했습니다.", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { }
            }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        // 260313 [feat]: MEMBER 테이블과 JOIN하여 휴대폰 번호 조회
        String sql = "SELECT o.*, m.phone " +
                     "FROM ORDERS o " +
                     "LEFT JOIN MEMBER m ON o.member_id = m.member_id " +
                     "ORDER BY o.order_date DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new Order(
                    rs.getLong("order_id"),
                    rs.getLong("member_id"),
                    rs.getString("phone"), // 휴대폰 번호 추가
                    rs.getInt("total_amount"),
                    rs.getInt("point_used"),
                    rs.getInt("point_earned"),
                    rs.getString("status"),
                    rs.getTimestamp("order_date")
                ));
            }
            return orders;
        } catch (SQLException e) {
            throw new RepositoryException("주문 목록 조회 중 오류가 발생했습니다.", e);
        }
    }
}
