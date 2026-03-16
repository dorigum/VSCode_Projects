package service;

import model.Menu;
import model.MenuOption;
import model.OptionSelection;
import model.OrderItem;

import java.util.List;

public class OrderItemService {
    public int calculateCartTotal(List<OrderItem> cart) {
        int totalAmount = 0;
        for (OrderItem orderItem : cart) {
            totalAmount += orderItem.getUnitPrice() * orderItem.getQuantity();
        }
        return totalAmount;
    }

    public OrderItem createOrderItem(Menu menu, int quantity, String categoryName, OptionSelection selection) {
        int unitPrice = menu.getPrice() + calculateOptionPrice(selection);
        return new OrderItem(0, 0, menu.getMenuId(), quantity, unitPrice,
                menu.getMenuName(), categoryName, selection.getSelectedOptions());
    }

    private int calculateOptionPrice(OptionSelection selection) {
        if (selection == null) {
            return 0;
        }

        int optionPrice = 0;
        for (MenuOption option : selection.getSelectedOptions()) {
            optionPrice += option.getExtraPrice();
        }
        return optionPrice;
    }
}
