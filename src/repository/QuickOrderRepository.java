package repository;

import exception.RepositoryException;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuickOrderRepository {

    // 가장 최근 주문의 ORDER_ITEM 목록 조회
    public List<OrderItem> getLastOrderItems(long memberId) {
        List<OrderItem> list = new ArrayList<>();

        // 1. 가장 최근 order_id 조회
        String orderSql = "SELECT order_id FROM ORDERS " +
                "WHERE member_id = ? AND status = 'COMPLETED' " +
                "ORDER BY order_date DESC LIMIT 1";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return list;
                }

                long lastOrderId = rs.getLong("order_id");

                // 2. 해당 order_id의 ORDER_ITEM 목록 조회
                String itemSql = "SELECT oi.order_item_id, oi.order_id, oi.menu_id, " +
                        "oi.quantity, oi.unit_price, m.menu_name, c.category_name " +
                        "FROM ORDER_ITEM oi " +
                        "JOIN MENU m ON oi.menu_id = m.menu_id " +
                        "JOIN CATEGORY c ON m.category_id = c.category_id " +
                        "WHERE oi.order_id = ?";

                try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                    itemPs.setLong(1, lastOrderId);
                    try (ResultSet itemRs = itemPs.executeQuery()) {
                        while (itemRs.next()) {
                            list.add(new OrderItem(
                                    itemRs.getLong("order_item_id"),
                                    itemRs.getLong("order_id"),
                                    itemRs.getLong("menu_id"),
                                    itemRs.getInt("quantity"),
                                    itemRs.getInt("unit_price"),
                                    itemRs.getString("menu_name"),
                                    itemRs.getString("category_name")
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("퀵오더 조회 중 오류가 발생했습니다.", e);
        }
        return list;
    }
}
