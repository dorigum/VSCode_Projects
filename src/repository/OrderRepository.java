package repository;

import model.Order;

import java.util.List;
import java.util.Map;

public interface OrderRepository {
    int getTotalSales();

    Map<String, Integer> getSalesByCategory();

    Map<String, Integer> getSalesByMenu();

    List<String> getTopSellingMenus();

    Map<String, Integer> getDailySales();

    Map<String, Integer> getWeeklySales();

    Map<String, Integer> getMonthlySales();

    Map<String, Integer> getYearlySales();

    boolean cancelOrder(long orderId);

    List<Order> getAllOrders();
}
