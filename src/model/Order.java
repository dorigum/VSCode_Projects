package model;

import java.util.Date;
import java.util.List;

public class Order {
	private long orderId;
	private long memberId;
	private String memberPhone; // 신규: 주문자 휴대폰 번호
	private int totalAmount;
	private int pointUsed;
	private int pointEarned;
	private String status;
	private Date orderDate;
	private List<OrderItem> items;

	public Order(long orderId, long memberId, int totalAmount, int pointUsed, int pointEarned, String status,
			Date orderDate) {
		this(orderId, memberId, null, totalAmount, pointUsed, pointEarned, status, orderDate);
	}

	public Order(long orderId, long memberId, String memberPhone, int totalAmount, int pointUsed, int pointEarned,
			String status, Date orderDate) {
		this.orderId = orderId;
		this.memberId = memberId;
		this.memberPhone = memberPhone;
		this.totalAmount = totalAmount;
		this.pointUsed = pointUsed;
		this.pointEarned = pointEarned;
		this.status = status;
		this.orderDate = orderDate;
	}

	public long getOrderId() {
		return orderId;
	}

	public long getMemberId() {
		return memberId;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public int getPointUsed() {
		return pointUsed;
	}

	public int getPointEarned() {
		return pointEarned;
	}

	public String getStatus() {
		return status;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String memberInfo = (memberPhone != null) ? memberPhone : (memberId > 0 ? "회원ID: " + memberId : "비회원");
		sb.append(String.format("[주문] %s | 주문자: %s | 총액: %,d원 | 상태: %s\n", 
				orderDate, memberInfo, totalAmount, status));
		sb.append(String.format("      사용포인트: %,d원 | 적립포인트: %,d원\n", pointUsed, pointEarned));
		
		if (items != null) {
			for (OrderItem item : items) {
				sb.append(String.format("  - %-15s %d개 x %,d원\n", item.getMenuNameSnapshot(), item.getQuantity(),
						item.getUnitPrice()));
				
				// 선택된 세부 옵션 출력 (추가된 로직)
				List<MenuOption> options = item.getOptions();
				if (options != null && !options.isEmpty()) {
					String optStr = options.stream()
							.map(MenuOption::getOptionName)
							.collect(java.util.stream.Collectors.joining(", "));
					sb.append(String.format("    └─ [선택 옵션: %s]\n", optStr));
				}
			}
		}
		return sb.toString();
	}
}
