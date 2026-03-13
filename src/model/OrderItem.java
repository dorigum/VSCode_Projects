package model;

import java.sql.Timestamp;
import java.util.List;

public class OrderItem {
	private long orderItemId;
	private long orderId;
	private long menuId;
	private int quantity;
	private int unitPrice;
	private String menuNameSnapshot;
	private String categoryNameSnapshot;
	private List<MenuOption> options;
	private int pointUsed;
	private int pointEarned;
	private Timestamp orderDate;

	public OrderItem(long orderItemId, long orderId, long menuId, int quantity, int unitPrice, String menuNameSnapshot,
			String categoryNameSnapshot, int pointUsed, int pointEarned, Timestamp orderDate) {
		this.orderItemId = orderItemId;
		this.orderId = orderId;
		this.menuId = menuId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.menuNameSnapshot = menuNameSnapshot;
		this.categoryNameSnapshot = categoryNameSnapshot;
		this.pointUsed = pointUsed;
		this.pointEarned = pointEarned;
		this.orderDate = orderDate;
	}

	public OrderItem(long orderItemId, long orderId, long menuId, int quantity, int unitPrice, String menuNameSnapshot,
			String categoryNameSnapshot) {
		this.orderItemId = orderItemId;
		this.orderId = orderId;
		this.menuId = menuId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.menuNameSnapshot = menuNameSnapshot;
		this.categoryNameSnapshot = categoryNameSnapshot;
	}

	public OrderItem(long orderItemId, long orderId, long menuId, int quantity, int unitPrice, String menuNameSnapshot,
			String categoryNameSnapshot, List<MenuOption> options) {
		this.orderItemId = orderItemId;
		this.orderId = orderId;
		this.menuId = menuId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.menuNameSnapshot = menuNameSnapshot;
		this.categoryNameSnapshot = categoryNameSnapshot;
		this.options = options;
	}

	public long getOrderItemId() {
		return orderItemId;
	}

	public long getOrderId() {
		return orderId;
	}

	public long getMenuId() {
		return menuId;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public String getMenuNameSnapshot() {
		return menuNameSnapshot;
	}

	public String getCategoryNameSnapshot() {
		return categoryNameSnapshot;
	}

	public List<MenuOption> getOptions() {
		return options;
	}

	public int getPointUsed() {
		return pointUsed;
	}

	public int getPointEarned() {
		return pointEarned;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	@Override
	public String toString() {
		return String.format("%s (%d개, 단가: %,d원) | 주문일: %s | 사용포인트: %,d원 | 적립포인트: %,d원", menuNameSnapshot, quantity,
				unitPrice, orderDate, pointUsed, pointEarned);
	}
}
