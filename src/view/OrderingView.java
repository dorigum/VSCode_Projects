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
    private static final int SHOW_CART = 8;
    private static final int CONFIRM_SELECTION = 9;
    private static final int PLACE_ORDER = 1;

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
            if (categoryChoice == SHOW_CART) {
                EndView.printCart(cart);
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
            if (menuChoice == SHOW_CART) {
                EndView.printCart(cart);
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
        int orderChoice = readInt("주문하시겠습니까? 1. 주문, 0. 뒤로: ");
        if (orderChoice == PLACE_ORDER) {
            int result = menuController.order(cart, member);
            if (result == 1) {
                EndView.success("주문이 완료되었습니다.");
                cart.clear();
                return true;
            }
            FailView.fail("주문에 실패했습니다.");
        }
        return false;
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
