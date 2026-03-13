package model;

import java.util.Date;

public class Order {
    private long orderId;
    private long memberId;
    private String memberPhone; // 신규: 주문자 휴대폰 번호
    private int totalAmount;
    private int pointUsed;
    private int pointEarned;
    private String status;
    private Date orderDate;

    public Order(long orderId, long memberId, int totalAmount, int pointUsed, int pointEarned, String status, Date orderDate) {
        this(orderId, memberId, null, totalAmount, pointUsed, pointEarned, status, orderDate);
    }

    public Order(long orderId, long memberId, String memberPhone, int totalAmount, int pointUsed, int pointEarned, String status, Date orderDate) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.memberPhone = memberPhone;
        this.totalAmount = totalAmount;
        this.pointUsed = pointUsed;
        this.pointEarned = pointEarned;
        this.status = status;
        this.orderDate = orderDate;
    }

    public long getOrderId() { return orderId; }
    public long getMemberId() { return memberId; }
    public String getMemberPhone() { return memberPhone; }
    public int getTotalAmount() { return totalAmount; }
    public int getPointUsed() { return pointUsed; }
    public int getPointEarned() { return pointEarned; }
    public String getStatus() { return status; }
    public Date getOrderDate() { return orderDate; }

    @Override
    public String toString() {
        String mark = "[ ]";
        if ("CANCELLED".equals(status)) {
            mark = "[X]";
        } else if ("COMPLETED".equals(status)) {
            mark = "[V]";
        }
        
        String memberInfo = "비회원";
        if (memberId > 0) {
            memberInfo = (memberPhone != null) ? memberPhone : "회원(" + memberId + ")";
        }
        
        return String.format("%s 주문번호: %d | 주문자: %-15s | 총액: %,d원 | 상태: %-10s | 주문일: %s", 
                mark, orderId, memberInfo, totalAmount, status, orderDate);
    }
}
