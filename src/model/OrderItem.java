package model;

public class OrderItem {
    private long orderItemId;
    private long orderId;
    private long menuId;
    private int quantity;
    private int unitPrice;
    private String menuNameSnapshot;
    private String categoryNameSnapshot;

    public OrderItem(long orderItemId, long orderId, long menuId, int quantity, int unitPrice, String menuNameSnapshot, String categoryNameSnapshot) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.menuNameSnapshot = menuNameSnapshot;
        this.categoryNameSnapshot = categoryNameSnapshot;
    }

    public long getOrderItemId() { return orderItemId; }
    public long getOrderId() { return orderId; }
    public long getMenuId() { return menuId; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public String getMenuNameSnapshot() { return menuNameSnapshot; }
    public String getCategoryNameSnapshot() { return categoryNameSnapshot; }

    @Override
    public String toString() {
        return String.format("%s (%d개, 단가: %d원)", menuNameSnapshot, quantity, unitPrice);
    }
}
