package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.Member;
import model.Menu;
import model.MenuOption;
import model.OptionGroup;
import model.OrderItem;
import repository.MenuOptionRepository;
import repository.MenuOptionRepositoryImpl;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import repository.OptionGroupRepository;
import repository.OptionGroupRepositoryImpl;
import repository.OrderRepository;
import repository.OrderRepositoryImpl;

import java.util.List;

public class MenuServiceImpl implements MenuService {
    private static final int DEFAULT_MENU_LIMIT = 5;

    private final MenuRepository menuRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final OrderRepository orderRepository;

    public MenuServiceImpl() {
        this(
                new MenuRepositoryImpl(),
                new OptionGroupRepositoryImpl(),
                new MenuOptionRepositoryImpl(),
                new OrderRepositoryImpl()
        );
    }

    public MenuServiceImpl(MenuRepository menuRepository) {
        this(menuRepository, new OptionGroupRepositoryImpl(), new MenuOptionRepositoryImpl(), new OrderRepositoryImpl());
    }

    public MenuServiceImpl(MenuRepository menuRepository, OptionGroupRepository optionGroupRepository,
            MenuOptionRepository menuOptionRepository, OrderRepository orderRepository) {
        if (menuRepository == null) {
            throw new ValidationException("MenuRepository는 null일 수 없습니다.");
        }
        if (optionGroupRepository == null) {
            throw new ValidationException("OptionGroupRepository는 null일 수 없습니다.");
        }
        if (menuOptionRepository == null) {
            throw new ValidationException("MenuOptionRepository는 null일 수 없습니다.");
        }
        if (orderRepository == null) {
            throw new ValidationException("OrderRepository는 null일 수 없습니다.");
        }
        this.menuRepository = menuRepository;
        this.optionGroupRepository = optionGroupRepository;
        this.menuOptionRepository = menuOptionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Menu> getMenusByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ValidationException("카테고리명은 비어 있을 수 없습니다.");
        }
        List<Menu> menus = menuRepository.getMenusByCategoryName(categoryName);
        if (menus == null || menus.isEmpty()) {
            throw new NotFoundException(categoryName + " 카테고리의 메뉴가 없습니다.");
        }
        return menus;
    }

    @Override
    public List<Menu> getPopularMenus() {
        List<Menu> menus = menuRepository.getPopularMenus(DEFAULT_MENU_LIMIT);
        if (menus == null || menus.isEmpty()) {
            return getLatestMenus();
        }
        return menus;
    }

    @Override
    public List<Menu> getLatestMenus() {
        return menuRepository.getLatestMenus(DEFAULT_MENU_LIMIT);
    }

    @Override
    public List<OptionGroup> getOptionGroups(Menu menu) {
        if (menu == null) {
            throw new ValidationException("메뉴 정보가 없습니다.");
        }
        if (menu.getMenuId() <= 0) {
            throw new ValidationException("유효하지 않은 메뉴입니다.");
        }
        return optionGroupRepository.findOptionGroupsWithOptionsByMenuId(menu.getMenuId());
    }

    @Override
    public List<MenuOption> getOptionsByGroup(OptionGroup optionGroup) {
        if (optionGroup == null) {
            throw new ValidationException("옵션 그룹 정보가 없습니다.");
        }
        if (optionGroup.getGroupId() <= 0) {
            throw new ValidationException("유효하지 않은 옵션 그룹입니다.");
        }
        return menuOptionRepository.findByGroupId(optionGroup.getGroupId());
    }

    @Override
    public int placeOrder(List<OrderItem> orderItems, Member member, int pointUsed) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new ValidationException("주문할 메뉴가 없습니다.");
        }

        int totalAmount = 0;
        for (OrderItem orderItem : orderItems) {
            if (orderItem == null) {
                throw new ValidationException("유효하지 않은 주문 항목이 있습니다.");
            }
            if (orderItem.getMenuId() <= 0) {
                throw new ValidationException("유효하지 않은 메뉴가 포함되어 있습니다.");
            }
            if (orderItem.getQuantity() <= 0) {
                throw new ValidationException("주문 수량은 1개 이상이어야 합니다.");
            }
            if (orderItem.getUnitPrice() < 0) {
                throw new ValidationException("유효하지 않은 주문 금액입니다.");
            }
            totalAmount += orderItem.getUnitPrice() * orderItem.getQuantity();
        }

        if (pointUsed < 0) {
            throw new ValidationException("사용 포인트는 0 이상이어야 합니다.");
        }
        if (pointUsed > totalAmount) {
            throw new ValidationException("사용 포인트는 주문 금액을 초과할 수 없습니다.");
        }
        if (member == null && pointUsed > 0) {
            throw new ValidationException("비회원은 포인트를 사용할 수 없습니다.");
        }
        if (member != null && pointUsed > member.getPointBalance()) {
            throw new ValidationException("보유 포인트를 초과해 사용할 수 없습니다.");
        }

        return orderRepository.placeOrder(orderItems, member, pointUsed);
    }
}
