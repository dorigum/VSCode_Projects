package repository;

import model.Order;

import java.util.List;
import java.util.Map;

public interface OrderRepository {
    int getTotalSales();

    Map<String, Integer> getSalesByCategory();

    List<String> getTopSellingMenus();

    Map<String, Integer> getDailySales();

    boolean cancelOrder(long orderId);

    List<Order> getAllOrders();
}
