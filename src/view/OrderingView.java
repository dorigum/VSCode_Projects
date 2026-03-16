package view;

import controller.MenuController;
import model.Member;
import model.Menu;
import model.MenuOption;
import model.OptionGroup;
import model.OptionSelection;
import model.OrderItem;
import service.OptionSelectionService;
import service.OrderItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderingView {
    private static final int BACK = 0;
    private static final int MANAGE_CART = 8;
    private static final int CONFIRM_SELECTION = 9;
    private static final int PLACE_ORDER = 1;
    private static final int REMOVE_CART_ITEM = 1;
    private static final int CHANGE_CART_QUANTITY = 2;
    private static final int CLEAR_CART = 3;

    private final Scanner scanner;
    private final OptionSelectionService optionSelectionService;
    private final OrderItemService orderItemService;

    public OrderingView(Scanner scanner) {
        this.scanner = scanner;
        this.optionSelectionService = new OptionSelectionService();
        this.orderItemService = new OrderItemService();
    }

    public void run(MenuController menuController, Member member) {
        List<OrderItem> cart = new ArrayList<>();
        while (true) {
            printCategoryMenu();
            int categoryChoice = readInt("선택: ");

            if (categoryChoice == BACK) {
                return;
            }
            if (categoryChoice == MANAGE_CART) {
                if (runCartManagementLoop(menuController, cart, member)) {
                    return;
                }
                continue;
            }
            if (categoryChoice == CONFIRM_SELECTION) {
                if (confirmOrder(menuController, cart, member)) {
                    return;
                }
                continue;
            }

            List<Menu> menus = loadMenus(menuController, categoryChoice);
            if (menus == null) {
                FailView.fail("잘못된 선택입니다.");
                continue;
            }
            if (menus.isEmpty()) {
                FailView.fail("메뉴가 없습니다.");
                continue;
            }

            if (runMenuSelectionLoop(menuController, menus, cart, member)) {
                return;
            }
        }
    }

    private boolean runMenuSelectionLoop(MenuController menuController, List<Menu> menus,
            List<OrderItem> cart, Member member) {
        EndView.printOrderMenu(menus);
        while (true) {
            int menuChoice = readInt("메뉴 선택 (0. 뒤로, 8. 카트확인, 9. 주문하기): ");
            if (menuChoice == BACK) {
                return false;
            }
            if (menuChoice == MANAGE_CART) {
                EndView.printOrderMenu(menus);
                if (runCartManagementLoop(menuController, cart, member)) {
                    return true;
                }
                continue;
            }
            if (menuChoice == CONFIRM_SELECTION) {
                if (confirmOrder(menuController, cart, member)) {
                    return true;
                }
                continue;
            }

            Menu selectedMenu = findMenuByChoice(menus, menuChoice);
            if (selectedMenu == null) {
                FailView.fail("올바른 번호를 선택해주세요.");
                continue;
            }

            addMenuToCart(menuController, selectedMenu, cart);
            EndView.printOrderMenu(menus);
        }
    }

    private boolean runCartManagementLoop(MenuController menuController, List<OrderItem> cart, Member member) {
        while (true) {
            EndView.printCart(cart);
            EndView.printCartManagementMenu();

            int choice = readInt("장바구니 관리 선택 (0. 뒤로, 9. 주문하기): ");
            if (choice == BACK) {
                return false;
            }
            if (choice == CONFIRM_SELECTION) {
                return confirmOrder(menuController, cart, member);
            }
            if (cart.isEmpty()) {
                FailView.fail("장바구니가 비어 있습니다.");
                continue;
            }

            if (choice == REMOVE_CART_ITEM) {
                removeCartItem(cart);
                continue;
            }
            if (choice == CHANGE_CART_QUANTITY) {
                changeCartItemQuantity(cart);
                continue;
            }
            if (choice == CLEAR_CART) {
                clearCart(cart);
                continue;
            }

            FailView.fail("올바른 번호를 선택해주세요.");
        }
    }

    private OptionSelection selectMenuOptions(List<OptionGroup> optionGroups) {
        OptionSelection selection = optionSelectionService.createDefaultSelection(optionGroups);
        if (optionGroups == null || optionGroups.isEmpty()) {
            return selection;
        }

        while (true) {
            EndView.printSelectedOptionGroups(optionGroups, selection);
            int groupChoice = readInt("옵션 변경할 그룹 번호 (0. 뒤로, 9. 선택 확정): ");

            if (groupChoice == BACK) {
                return null;
            }
            if (groupChoice == CONFIRM_SELECTION) {
                return selection;
            }

            OptionGroup targetGroup = findOptionGroupByChoice(optionGroups, groupChoice);
            if (targetGroup == null) {
                FailView.fail("올바른 그룹 번호를 선택해주세요.");
                continue;
            }

            OptionSelection updatedSelection = changeOptionSelection(targetGroup, selection);
            if (updatedSelection == null) {
                return selection;
            }
            selection = updatedSelection;
        }
    }

    private OptionSelection changeOptionSelection(OptionGroup optionGroup, OptionSelection selection) {
        List<MenuOption> options = optionGroup.getOptions();
        if (options == null || options.isEmpty()) {
            FailView.fail("옵션 목록이 없습니다.");
            return selection;
        }

        EndView.printSelectableMenuOptions(optionGroup, selection);

        while (true) {
            int optionChoice = readInt("옵션 선택 (0. 그룹 선택으로, 9. 선택 확정): ");
            if (optionChoice == BACK) {
                return selection;
            }
            if (optionChoice == CONFIRM_SELECTION) {
                return null;
            }

            MenuOption selectedOption = findOptionByChoice(options, optionChoice);
            if (selectedOption == null) {
                FailView.fail("올바른 옵션을 선택해주세요.");
                continue;
            }

            return optionSelectionService.changeSelection(selection, optionGroup, selectedOption);
        }
    }

    private boolean confirmOrder(MenuController menuController, List<OrderItem> cart, Member member) {
        if (cart.isEmpty()) {
            FailView.fail("장바구니가 비어 있습니다.");
            return false;
        }

        EndView.printCart(cart);
        int totalAmount = calculateCartTotal(cart);
        int pointUsed = readPointUsage(member, totalAmount);
        int finalAmount = totalAmount - pointUsed;

        if (member != null) {
            System.out.printf("보유 포인트: %,d원%n", member.getPointBalance());
            System.out.printf("사용 포인트: %,d원%n", pointUsed);
        }
        System.out.printf("결제 예정 금액: %,d원%n", finalAmount);
        int orderChoice = readInt("주문하시겠습니까? 1. 주문, 0. 뒤로: ");
        if (orderChoice == PLACE_ORDER) {
            int result = menuController.order(cart, member, pointUsed);
            if (result == 1) {
                updateMemberPointBalance(member, totalAmount, pointUsed);
                EndView.success("주문이 완료되었습니다.");
                cart.clear();
                return true;
            }
            FailView.fail("주문에 실패했습니다.");
        }
        return false;
    }

    private int calculateCartTotal(List<OrderItem> cart) {
        int totalAmount = 0;
        for (OrderItem orderItem : cart) {
            totalAmount += orderItem.getUnitPrice() * orderItem.getQuantity();
        }
        return totalAmount;
    }

    private int readPointUsage(Member member, int totalAmount) {
        if (member == null) {
            return 0;
        }

        int availablePoint = member.getPointBalance();
        System.out.printf("현재 보유 포인트: %,d원%n", availablePoint);
        while (true) {
            int pointUsed = readInt("사용할 포인트 입력 (0. 사용 안 함): ");
            if (pointUsed < 0) {
                FailView.fail("0 이상의 숫자를 입력해 주세요.");
                continue;
            }
            if (pointUsed > availablePoint) {
                FailView.fail("보유 포인트를 초과해 사용할 수 없습니다.");
                continue;
            }
            if (pointUsed > totalAmount) {
                FailView.fail("주문 금액을 초과해 사용할 수 없습니다.");
                continue;
            }
            return pointUsed;
        }
    }

    private void updateMemberPointBalance(Member member, int totalAmount, int pointUsed) {
        if (member == null) {
            return;
        }

        int pointEarned = Math.max(0, (totalAmount - pointUsed) / 10);
        member.setPointBalance(member.getPointBalance() - pointUsed + pointEarned);
    }

    private List<Menu> loadMenus(MenuController menuController, int categoryChoice) {
        switch (categoryChoice) {
            case 1:
                return menuController.getPopularMenuList();
            case 2:
                return menuController.getLatestMenuList();
            case 3:
                return menuController.getMenusByCategory("커피");
            case 4:
                return menuController.getMenusByCategory("논커피");
            case 5:
                return menuController.getMenusByCategory("디저트");
            default:
                return null;
        }
    }

    private void addMenuToCart(MenuController menuController, Menu selectedMenu, List<OrderItem> cart) {
        List<OptionGroup> optionGroups = menuController.getOptionGroups(selectedMenu);
        OptionSelection selection = selectMenuOptions(optionGroups);
        if (selection == null) {
            return;
        }

        int quantity = readQuantity("개수 선택 (0. 뒤로): ");
        if (quantity == BACK) {
            return;
        }

        cart.add(createOrderItem(menuController, selectedMenu, quantity, selection));
        System.out.println("장바구니에 담겼습니다.");
    }

    private OrderItem createOrderItem(MenuController menuController, Menu selectedMenu, int quantity,
            OptionSelection selection) {
        String categorySnapshot = menuController.getCategoryName(selectedMenu);
        return orderItemService.createOrderItem(selectedMenu, quantity, categorySnapshot, selection);
    }

    private void removeCartItem(List<OrderItem> cart) {
        int cartIndex = readCartItemIndex(cart, "삭제할 상품 번호 (0. 뒤로): ");
        if (cartIndex == BACK) {
            return;
        }

        OrderItem removedItem = cart.remove(cartIndex - 1);
        EndView.success(removedItem.getMenuNameSnapshot() + " 삭제가 완료되었습니다.");
    }

    private void changeCartItemQuantity(List<OrderItem> cart) {
        int cartIndex = readCartItemIndex(cart, "수량을 변경할 상품 번호 (0. 뒤로): ");
        if (cartIndex == BACK) {
            return;
        }

        int quantity = readPositiveQuantity("새 수량 입력 (1 이상, 0. 뒤로): ");
        if (quantity == BACK) {
            return;
        }

        int itemIndex = cartIndex - 1;
        OrderItem currentItem = cart.get(itemIndex);
        cart.set(itemIndex, copyOrderItemWithQuantity(currentItem, quantity));
        EndView.success(currentItem.getMenuNameSnapshot() + " 수량이 변경되었습니다.");
    }

    private void clearCart(List<OrderItem> cart) {
        int clearChoice = readInt("장바구니를 비우시겠습니까? 1. 예, 0. 아니오: ");
        if (clearChoice != PLACE_ORDER) {
            return;
        }

        cart.clear();
        EndView.success("장바구니를 비웠습니다.");
    }

    private OrderItem copyOrderItemWithQuantity(OrderItem orderItem, int quantity) {
        return new OrderItem(
                orderItem.getOrderItemId(),
                orderItem.getOrderId(),
                orderItem.getMenuId(),
                quantity,
                orderItem.getUnitPrice(),
                orderItem.getMenuNameSnapshot(),
                orderItem.getCategoryNameSnapshot(),
                orderItem.getOptions());
    }

    private int readCartItemIndex(List<OrderItem> cart, String prompt) {
        while (true) {
            int cartChoice = readInt(prompt);
            if (cartChoice == BACK) {
                return BACK;
            }
            if (cartChoice < 1 || cartChoice > cart.size()) {
                FailView.fail("올바른 상품 번호를 선택해주세요.");
                continue;
            }
            return cartChoice;
        }
    }

    private Menu findMenuByChoice(List<Menu> menus, int menuChoice) {
        if (menuChoice < 1 || menuChoice > menus.size()) {
            return null;
        }
        return menus.get(menuChoice - 1);
    }

    private OptionGroup findOptionGroupByChoice(List<OptionGroup> optionGroups, int groupChoice) {
        if (groupChoice < 1 || groupChoice > optionGroups.size()) {
            return null;
        }
        return optionGroups.get(groupChoice - 1);
    }

    private MenuOption findOptionByChoice(List<MenuOption> options, int optionChoice) {
        if (optionChoice < 1 || optionChoice > options.size()) {
            return null;
        }
        return options.get(optionChoice - 1);
    }

    private int readQuantity(String prompt) {
        while (true) {
            int quantity = readInt(prompt);
            if (quantity == BACK) {
                return BACK;
            }
            if (quantity < 0) {
                FailView.fail("0 이상의 숫자를 입력해 주세요.");
                continue;
            }
            return quantity;
        }
    }

    private int readPositiveQuantity(String prompt) {
        while (true) {
            int quantity = readInt(prompt);
            if (quantity == BACK) {
                return BACK;
            }
            if (quantity < 1) {
                FailView.fail("1 이상의 숫자를 입력해 주세요.");
                continue;
            }
            return quantity;
        }
    }

    private void printCategoryMenu() {
        System.out.println("\n--- [메뉴 카테고리] ---");
        System.out.println("1. 인기 상품");
        System.out.println("2. 신상품");
        System.out.println("3. 커피");
        System.out.println("4. 논커피");
        System.out.println("5. 디저트");
        System.out.println("8. 카트확인");
        System.out.println("9. 주문하기");
        System.out.println("0. 뒤로가기");
    }

    private int readInt(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                FailView.fail("숫자를 입력해 주세요.");
            }
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
