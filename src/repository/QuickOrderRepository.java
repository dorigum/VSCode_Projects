package repository;

import exception.RepositoryException;
import model.MenuOption;
import model.Order;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuickOrderRepository {

	public Order getRecentOrder(long memberId) {
		String orderSql = "SELECT * FROM ORDERS " + "WHERE member_id = ? AND status = 'COMPLETED' "
				+ "ORDER BY order_date DESC LIMIT 1";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(orderSql)) {

			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next())
					return null;

				Order order = new Order(rs.getLong("order_id"), rs.getLong("member_id"), rs.getInt("total_amount"),
						rs.getInt("point_used"), rs.getInt("point_earned"), rs.getString("status"),
						rs.getTimestamp("order_date") // Date 타입이라 getTimestamp 사용
				);
				// ORDER_ITEM 조회
				String itemSql = "SELECT * FROM ORDER_ITEM WHERE order_id = ?";
				try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
					itemPs.setLong(1, order.getOrderId());
					try (ResultSet itemRs = itemPs.executeQuery()) {
						List<OrderItem> items = new ArrayList<>();
						while (itemRs.next()) {
							OrderItem item = new OrderItem(itemRs.getLong("order_item_id"), itemRs.getLong("order_id"),
									itemRs.getLong("menu_id"), itemRs.getInt("quantity"), itemRs.getInt("unit_price"),
									itemRs.getString("menu_name_snapshot"), itemRs.getString("category_name_snapshot"));

							// 옵션 조회 추가!
							String optSql = "SELECT mo.option_id, mo.group_id, mo.option_name, mo.extra_price "
									+ "FROM ORDER_ITEM_OPTION oio "
									+ "JOIN MENU_OPTION mo ON oio.option_id = mo.option_id "
									+ "WHERE oio.order_item_id = ?";
							try (PreparedStatement optPs = conn.prepareStatement(optSql)) {
								optPs.setLong(1, item.getOrderItemId());
								try (ResultSet optRs = optPs.executeQuery()) {
									List<MenuOption> options = new ArrayList<>();
									while (optRs.next()) {
										options.add(
												new MenuOption(optRs.getLong("option_id"), optRs.getLong("group_id"),
														optRs.getString("option_name"), optRs.getInt("extra_price")));
									}
									item.setOptions(options);
								}
							}
							items.add(item);
						}
						order.setItems(items);
					}
				}
				return order;
			}
		} catch (SQLException e) {
			throw new RepositoryException("퀵오더 조회 중 오류가 발생했습니다.", e);
		}
	}
}