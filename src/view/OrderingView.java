package view;

import controller.MenuController;
import model.Member;
import model.Menu;
import model.MenuOption;
import model.OptionGroup;
import model.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderingView {
    private final Scanner scanner;

    public OrderingView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(MenuController menuController, Member member) {
        List<OrderItem> cart = new ArrayList<>();
        while (true) {
            System.out.println("\n--- [메뉴 카테고리] ---");
            System.out.println("1. 인기 상품");
            System.out.println("2. 신상품");
            System.out.println("3. 커피");
            System.out.println("4. 논커피");
            System.out.println("5. 디저트");
            System.out.println("8. 카트확인");
            System.out.println("9. 주문하기");
            System.out.println("0. 뒤로가기");
            int sub = readInt("선택: ");

            List<Menu> menus = null;
            if (sub == 1) {
                menus = menuController.getPopularMenuList();
            } else if (sub == 2) {
                menus = menuController.getLatestMenuList();
            } else if (sub == 3) {
                menus = menuController.getMenusByCategory("커피");
            } else if (sub == 4) {
                menus = menuController.getMenusByCategory("논커피");
            } else if (sub == 5) {
                menus = menuController.getMenusByCategory("디저트");
            } else if (sub == 8) {
                EndView.printCart(cart);
            } else if (sub == 9) {
                runOrderFlow(menuController, cart, member);
                break;
            } else if (sub == 0) {
                break;
            } else {
                FailView.fail("잘못된 선택입니다.");
            }
            if (menus.isEmpty()) {
                FailView.fail("메뉴가 없습니다.");
                continue;
            }
            if (menus != null) {
                cart = runMenuSelectFlow(menuController, menus, cart, member);
            }
        }
    }

    private List<OrderItem> runMenuSelectFlow(MenuController menuController, List<Menu> menus,
            List<OrderItem> cart, Member member) {
        EndView.printMenu(menus);
        while (true) {
            int menuChoice = readInt("메뉴 선택 (0. 뒤로): ");
            if (menuChoice == 0) break;
            if (menuChoice < 1 || menuChoice > menus.size()) {
                FailView.fail("올바른 번호를 선택해주세요.");
                continue;
            }

            Menu selectedMenu = menus.get(menuChoice - 1);            
            List<OptionGroup> optionGroups = menuController.getOptionGroups(selectedMenu);
            List<MenuOption> selectedOptions = new ArrayList<>();
            boolean optionSelectCancled = false;

            printOptionGroups(optionGroups);
            for (OptionGroup optionGroup : optionGroups) {
                EndView.printOptionGroup(optionGroup);
                List<MenuOption> options = menuController.getOptions(optionGroup);

                int optionChoice = readInt("옵션 선택 (0. 뒤로): ");
                if (optionChoice == 0) {
                    optionSelectCancled = true;
                    break;
                }
                if (optionChoice < 1 || optionChoice > options.size()) {
                    FailView.fail("올바른 옵션을 선택해주세요.");
                    optionSelectCancled = true;
                    break;
                }
                selectedOptions.add(options.get(optionChoice - 1));
            }

            if (optionSelectCancled) break;

            int quantity = readInt("개수 선택 (0. 뒤로): ");
            if (quantity <= 0) break;

            String categorySnapshot = menuController.getCategoryName(selectedMenu);
            cart.add(new OrderItem(0, 0, selectedMenu.getMenuId(), quantity, selectedMenu.getPrice(),
                    selectedMenu.getMenuName(), categorySnapshot, selectedOptions));
            System.out.println("장바구니에 담겼습니다.");
        }
        return cart;
    }

    private void runOrderFlow(MenuController menuController, List<OrderItem> cart, Member member) {
        if (cart.isEmpty()) {
            FailView.fail("장바구니가 비어 있습니다.");
            return;
        }

        EndView.printCart(cart);
        int sub = readInt("주문하시겠습니까? 1. 주문, 0. 뒤로: ");
        if (sub == 1) {
            int result = menuController.order(cart, member);
            if (result == 1) {
                EndView.success("주문이 완료되었습니다.");
                cart.clear();
            } else {
                FailView.fail("주문에 실패했습니다.");
            }
        }
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

    private void printOptionGroups(List<OptionGroup> optionGroups){
        System.out.println("\n옵션을 선택해 주세요.");
        if (optionGroups == null || optionGroups.isEmpty()) {
            System.out.println("등록된 옵션 그룹이 없습니다.");
            return;
        }
        for (int i = 0; i < optionGroups.size(); i++) {
            OptionGroup optionGroup = optionGroups.get(i);
            List<MenuOption> options = optionGroup.getOptions();
            System.out.printf("%s: ",optionGroup.getGroupName());
            for (int j = 0; j < options.size(); j++) {
                MenuOption option = options.get(j);
                System.out.printf("%s,",  option.getOptionName());
            }
        }
    }
}

