package repository;

import exception.RepositoryException;
import model.Member;
import model.MenuOption;
import model.Order;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public int placeOrder(List<OrderItem> orderItems, Member member, int pointUsed) {
        String insertOrderSql = "INSERT INTO ORDERS (member_id, total_amount, point_used, point_earned, status) VALUES (?, ?, ?, ?, ?)";
        String insertOrderItemSql = "INSERT INTO ORDER_ITEM (order_id, menu_id, quantity, unit_price, menu_name_snapshot, category_name_snapshot) VALUES (?, ?, ?, ?, ?, ?)";
        String insertOrderItemOptionSql = "INSERT INTO ORDER_ITEM_OPTION (order_item_id, option_id) VALUES (?, ?)";
        String updateMemberPointSql = "UPDATE MEMBER SET point_balance = point_balance - ? + ? WHERE member_id = ?";
        String insertPointHistorySql = "INSERT INTO POINT_HISTORY (member_id, amount, reason) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int totalAmount = calculateTotalAmount(orderItems);
            int pointEarned = member == null ? 0 : Math.max(0, (totalAmount - pointUsed) / 10);
            long orderId = insertOrder(conn, insertOrderSql, member, totalAmount, pointUsed, pointEarned);

            for (OrderItem orderItem : orderItems) {
                long orderItemId = insertOrderItem(conn, insertOrderItemSql, orderId, orderItem);
                insertOrderItemOptions(conn, insertOrderItemOptionSql, orderItemId, orderItem.getOptions());
            }

            if (member != null) {
                if (pointUsed > 0 || pointEarned > 0) {
                    updateMemberPoint(conn, updateMemberPointSql, member.getMemberId(), pointUsed, pointEarned);
                }
                
                // 포인트 히스토리 기록
                if (pointUsed > 0) {
                    savePointHistory(conn, insertPointHistorySql, member.getMemberId(), -pointUsed, "주문 시 포인트 사용 (주문번호: " + orderId + ")");
                }
                if (pointEarned > 0) {
                    savePointHistory(conn, insertPointHistorySql, member.getMemberId(), pointEarned, "주문 결제 적립 (주문번호: " + orderId + ")");
                }
            }

            conn.commit();
            return 1;
        } catch (SQLException e) {
            rollback(conn);
            e.printStackTrace();
            throw new RepositoryException("주문 처리 중 오류가 발생했습니다.", e);
        } finally {
            closeConnection(conn);
        }
    }

    private void savePointHistory(Connection conn, String sql, long memberId, int amount, String reason) throws SQLException {
        // 테이블 존재 보장 (필요시)
        String checkSql = "CREATE TABLE IF NOT EXISTS POINT_HISTORY (" +
                "history_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "member_id BIGINT NOT NULL, " +
                "amount INT NOT NULL, " +
                "reason VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (member_id) REFERENCES MEMBER(member_id) ON DELETE CASCADE)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(checkSql);
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setInt(2, amount);
            pstmt.setString(3, reason);
            pstmt.executeUpdate();
        }
    }

    public long getTotalSales() {
        String sql = "SELECT SUM(total_amount) FROM ORDERS WHERE status = 'COMPLETED'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RepositoryException("총 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    public Map<String, Long> getSalesByCategory() {
        Map<String, Long> stats = new LinkedHashMap<>();
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
                stats.put(rs.getString("category_name_snapshot"), rs.getLong("sales"));
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

    public Map<String, Long> getDailySales() {
        return getSalesByPeriod("%Y-%m-%d");
    }

    public Map<String, Long> getSalesByPeriod(String format) {
        Map<String, Long> stats = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(order_date, ?) as period, SUM(total_amount) as total "
                     + "FROM ORDERS WHERE status = 'COMPLETED' "
                     + "GROUP BY period "
                     + "ORDER BY period ASC LIMIT 10";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, format);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("period"), rs.getLong("total"));
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
                    stats.put("count", rs.getLong("order_count"));
                    stats.put("amount", rs.getLong("total_amount"));
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("기간별 상세 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Map<Integer, Long> getHourlySales() {
        Map<Integer, Long> stats = new LinkedHashMap<>();
        String sql = "SELECT HOUR(order_date) as hour, SUM(total_amount) as total "
                     + "FROM ORDERS WHERE status = 'COMPLETED' "
                     + "GROUP BY hour ORDER BY hour";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getInt("hour"), rs.getLong("total"));
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("시간대별 매출 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Map<String, Long> getDayOfWeekSales() {
        Map<String, Long> stats = new LinkedHashMap<>();
        // 요일별 순서 보장을 위해 미리 키를 세팅 (월~일)
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String d : days) stats.put(d, 0L);

        String sql = "SELECT DAYNAME(order_date) as day, SUM(total_amount) as total "
                     + "FROM ORDERS WHERE status = 'COMPLETED' "
                     + "GROUP BY day";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("day"), rs.getLong("total"));
            }
            return stats;
        } catch (SQLException e) {
            throw new RepositoryException("요일별 매출 조회 중 오류가 발생했습니다.", e);
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
                    map.put("total", rs.getLong("total_spent"));
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
        // 1. 주문 기본 정보 및 주문자 정보 조회 (MEMBER JOIN) - 컬럼명 명시
        String orderSql = "SELECT o.order_id, o.member_id, o.total_amount, o.point_used, o.point_earned, o.status, o.order_date, m.phone " +
                         "FROM ORDERS o LEFT JOIN MEMBER m ON o.member_id = m.member_id ORDER BY o.order_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(orderSql)) {
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getLong("order_id"),
                    rs.getLong("member_id"),
                    rs.getString("phone"),
                    rs.getInt("total_amount"),
                    rs.getInt("point_used"),
                    rs.getInt("point_earned"),
                    rs.getString("status"),
                    rs.getTimestamp("order_date")
                );

                // 2. 각 주문에 대한 상세 아이템(OrderItem) 조회
                List<OrderItem> items = new ArrayList<>();
                String itemSql = "SELECT * FROM ORDER_ITEM WHERE order_id = ?";
                try (PreparedStatement itemPstmt = conn.prepareStatement(itemSql)) {
                    itemPstmt.setLong(1, order.getOrderId());
                    try (ResultSet itemRs = itemPstmt.executeQuery()) {
                        while (itemRs.next()) {
                            OrderItem item = new OrderItem(
                                itemRs.getLong("order_item_id"),
                                itemRs.getLong("order_id"),
                                itemRs.getLong("menu_id"),
                                itemRs.getInt("quantity"),
                                itemRs.getInt("unit_price"),
                                itemRs.getString("menu_name_snapshot"),
                                itemRs.getString("category_name_snapshot")
                            );

                            // 3. 각 아이템에 대한 선택된 옵션(MenuOption) 조회
                            List<MenuOption> options = new ArrayList<>();
                            String optionSql = "SELECT mo.* FROM MENU_OPTION mo " +
                                             "JOIN ORDER_ITEM_OPTION oio ON mo.option_id = oio.option_id " +
                                             "WHERE oio.order_item_id = ?";
                            try (PreparedStatement optPstmt = conn.prepareStatement(optionSql)) {
                                optPstmt.setLong(1, item.getOrderItemId());
                                try (ResultSet optRs = optPstmt.executeQuery()) {
                                    while (optRs.next()) {
                                        options.add(new MenuOption(
                                            optRs.getLong("option_id"),
                                            optRs.getLong("group_id"),
                                            optRs.getString("option_name"),
                                            optRs.getInt("extra_price"),
                                            optRs.getInt("display_order")
                                        ));
                                    }
                                }
                            }
                            item.setOptions(options); // 이제 setOptions 메서드를 사용할 수 있습니다.
                            items.add(item);
                        }
                    }
                }
                order.setItems(items);
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new RepositoryException("주문 목록 및 상세 내역 조회 중 오류가 발생했습니다.", e);
        }
    }

    private int calculateTotalAmount(List<OrderItem> orderItems) {
        int totalAmount = 0;
        for (OrderItem orderItem : orderItems) {
            totalAmount += orderItem.getUnitPrice() * orderItem.getQuantity();
        }
        return totalAmount;
    }

    private long insertOrder(Connection conn, String sql, Member member, int totalAmount, int pointUsed, int pointEarned)
            throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (member == null) {
                pstmt.setNull(1, Types.BIGINT);
            } else {
                pstmt.setLong(1, member.getMemberId());
            }
            pstmt.setInt(2, totalAmount);
            pstmt.setInt(3, pointUsed);
            pstmt.setInt(4, pointEarned);
            pstmt.setString(5, "COMPLETED");
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("주문 번호 생성에 실패했습니다.");
    }

    private long insertOrderItem(Connection conn, String sql, long orderId, OrderItem orderItem) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, orderId);
            pstmt.setLong(2, orderItem.getMenuId());
            pstmt.setInt(3, orderItem.getQuantity());
            pstmt.setInt(4, orderItem.getUnitPrice());
            pstmt.setString(5, orderItem.getMenuNameSnapshot());
            pstmt.setString(6, orderItem.getCategoryNameSnapshot());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("주문 항목 생성에 실패했습니다.");
    }

    private void insertOrderItemOptions(Connection conn, String sql, long orderItemId, List<MenuOption> options) throws SQLException {
        if (options == null || options.isEmpty()) {
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (MenuOption option : options) {
                pstmt.setLong(1, orderItemId);
                pstmt.setLong(2, option.getOptionId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void updateMemberPoint(Connection conn, String sql, long memberId, int pointUsed, int pointEarned) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pointUsed);
            pstmt.setInt(2, pointEarned);
            pstmt.setLong(3, memberId);
            pstmt.executeUpdate();
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
