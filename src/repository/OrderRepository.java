package repository;

import model.Member;
import model.Order;
import model.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderRepository {
    int getTotalSales();

    Map<String, Integer> getSalesByCategory();

    List<String> getTopSellingMenus();

    Map<String, Integer> getDailySales();

    Map<String, Integer> getSalesByPeriod(String format);

    // 260313 [feat]: 매출 통계 고도화 신규 메서드
    Map<String, Object> getSalesStatsByPeriod(String startDate, String endDate);
    Map<Integer, Integer> getHourlySales();
    Map<String, Integer> getDayOfWeekSales();
    List<Map<String, Object>> getTopSpenders(int limit);

    boolean cancelOrder(long orderId);

    List<Order> getAllOrders();

    int placeOrder(List<OrderItem> orderItems, Member member, int pointUsed);
}
