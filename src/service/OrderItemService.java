package service;

import model.Menu;
import model.OptionSelection;
import model.OrderItem;

public class OrderItemService {
    public OrderItem createOrderItem(Menu menu, int quantity, String categoryName, OptionSelection selection) {
        return new OrderItem(0, 0, menu.getMenuId(), quantity, menu.getPrice(),
                menu.getMenuName(), categoryName, selection.getSelectedOptions());
    }
}
