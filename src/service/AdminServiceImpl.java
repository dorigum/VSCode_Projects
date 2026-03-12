package service;

import exception.BusinessRuleException;
import exception.NotFoundException;
import exception.ValidationException;
import model.Category;
import model.Member;
import model.Menu;
import model.Order;
import repository.CategoryRepository;
import repository.CategoryRepositoryImpl;
import repository.MemberRepository;
import repository.MemberRepositoryImpl;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import repository.OrderRepository;
import repository.OrderRepositoryImpl;

import java.util.List;
import java.util.Map;

public class AdminServiceImpl implements AdminService {
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl() {
        this(new MenuRepositoryImpl(), new MemberRepositoryImpl(), new CategoryRepositoryImpl(), new OrderRepositoryImpl());
    }

    public AdminServiceImpl(MenuRepository menuRepository) {
        this(menuRepository, new MemberRepositoryImpl(), new CategoryRepositoryImpl(), new OrderRepositoryImpl());
    }

    public AdminServiceImpl(
        MenuRepository menuRepository,
        MemberRepository memberRepository,
        CategoryRepository categoryRepository,
        OrderRepository orderRepository
    ) {
        if (menuRepository == null) {
            throw new ValidationException("MenuRepository는 null일 수 없습니다.");
        }
        if (memberRepository == null) {
            throw new ValidationException("MemberRepository는 null일 수 없습니다.");
        }
        if (categoryRepository == null) {
            throw new ValidationException("CategoryRepository는 null일 수 없습니다.");
        }
        if (orderRepository == null) {
            throw new ValidationException("OrderRepository는 null일 수 없습니다.");
        }

        this.menuRepository = menuRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    public void registerMenu(int categoryId, String name, int price, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("메뉴명은 비어 있을 수 없습니다.");
        }
        if (price <= 0) {
            throw new ValidationException("가격은 1원 이상이어야 합니다.");
        }
        if (categoryId <= 0) {
            throw new ValidationException("카테고리 ID는 1 이상이어야 합니다.");
        }

        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null) {
            throw new NotFoundException("존재하지 않는 카테고리입니다.");
        }

        Menu menu = new Menu(categoryId, name.trim(), price, description == null ? "" : description.trim());
        if (!menuRepository.addMenu(menu)) {
            throw new BusinessRuleException("메뉴 등록에 실패했습니다.");
        }
    }

    public List<Menu> getMenuList() {
        return menuRepository.getAllMenus();
    }

    public void deleteMenu(long id) {
        if (id <= 0) {
            throw new ValidationException("메뉴 ID는 1 이상이어야 합니다.");
        }
        if (!menuRepository.deleteMenu(id)) {
            throw new NotFoundException("삭제할 메뉴가 없습니다.");
        }
    }

    public void addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("카테고리명은 비어 있을 수 없습니다.");
        }

        if (!categoryRepository.addCategory(name.trim())) {
            throw new BusinessRuleException("카테고리 등록에 실패했습니다.");
        }
    }

    public List<Category> getCategoryList() {
        return categoryRepository.getAllCategories();
    }

    public void deleteCategory(int id) {
        if (id <= 0) {
            throw new ValidationException("카테고리 ID는 1 이상이어야 합니다.");
        }
        if (!categoryRepository.deleteCategory(id)) {
            throw new NotFoundException("삭제할 카테고리가 없습니다.");
        }
    }

    public List<Member> getMemberList() {
        return memberRepository.getAllMembers();
    }

    public void deleteMember(long id) {
        if (id <= 0) {
            throw new ValidationException("회원 ID는 1 이상이어야 합니다.");
        }
        if (!memberRepository.deleteMember(id)) {
            throw new NotFoundException("삭제할 회원이 없습니다.");
        }
    }

    public List<Order> getOrderList() {
        return orderRepository.getAllOrders();
    }

    public void cancelOrder(long orderId) {
        if (orderId <= 0) {
            throw new ValidationException("주문 ID는 1 이상이어야 합니다.");
        }
        if (!orderRepository.cancelOrder(orderId)) {
            throw new NotFoundException("취소할 수 있는 주문이 없습니다.");
        }
    }

    public int getTotalSales() {
        return orderRepository.getTotalSales();
    }

    public Map<String, Integer> getSalesByCategory() {
        return orderRepository.getSalesByCategory();
    }

    public List<String> getTopSellingMenus() {
        return orderRepository.getTopSellingMenus();
    }

    public Map<String, Integer> getDailySales() {
        return orderRepository.getDailySales();
    }
}
