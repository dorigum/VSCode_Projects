package service;

import exception.BusinessRuleException;
import exception.NotFoundException;
import exception.ValidationException;
import model.*;
import repository.*;

import java.util.List;
import java.util.Map;

public class AdminServiceImpl implements AdminService {
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final MenuOptionRepository menuOptionRepository;

    public AdminServiceImpl() {
        this(
            new MenuRepositoryImpl(),
            new MemberRepositoryImpl(),
            new CategoryRepositoryImpl(),
            new OrderRepositoryImpl(),
            new OptionGroupRepositoryImpl(),
            new MenuOptionRepositoryImpl()
        );
    }

    public AdminServiceImpl(
        MenuRepository menuRepository,
        MemberRepository memberRepository,
        CategoryRepository categoryRepository,
        OrderRepository orderRepository,
        OptionGroupRepository optionGroupRepository,
        MenuOptionRepository menuOptionRepository
    ) {
        this.menuRepository = menuRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.optionGroupRepository = optionGroupRepository;
        this.menuOptionRepository = menuOptionRepository;
    }

    // --- 메뉴 관리 ---
    public void registerMenu(int categoryId, String name, int price, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("메뉴명은 비어 있을 수 없습니다.");
        }
        if (price <= 0) {
            throw new ValidationException("가격은 1원 이상이어야 합니다.");
        }
        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null) {
            throw new NotFoundException("존재하지 않는 카테고리 ID입니다.");
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
        if (!menuRepository.deleteMenu(id)) {
            throw new NotFoundException("삭제할 메뉴가 없습니다.");
        }
    }

    // --- 카테고리 관리 ---
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
        if (!categoryRepository.deleteCategory(id)) {
            throw new NotFoundException("삭제할 카테고리가 없습니다.");
        }
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryRepository.getCategoryById(id);
    }

    @Override
    public void addOptionGroupToCategory(int categoryId, long groupId, int displayOrder) {
        if (!categoryRepository.addOptionGroupToCategory(categoryId, groupId, displayOrder)) {
            throw new BusinessRuleException("카테고리별 옵션 그룹 등록에 실패했습니다.");
        }
    }

    @Override
    public void removeOptionGroupFromCategory(int categoryId, long groupId) {
        if (!categoryRepository.removeOptionGroupFromCategory(categoryId, groupId)) {
            throw new BusinessRuleException("카테고리별 옵션 그룹 삭제에 실패했습니다.");
        }
    }

    // --- 옵션 그룹 관리 ---
    @Override
    public List<OptionGroup> getOptionGroupList() {
        return optionGroupRepository.findAll();
    }

    @Override
    public void addOptionGroup(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("옵션 그룹명은 비어 있을 수 없습니다.");
        }
        optionGroupRepository.save(new OptionGroup(name.trim()));
    }

    @Override
    public void deleteOptionGroup(long groupId) {
        optionGroupRepository.delete(groupId);
    }

    // --- 메뉴 옵션 관리 ---
    @Override
    public List<MenuOption> getMenuOptionsByGroup(long groupId) {
        return menuOptionRepository.findByGroupId(groupId);
    }

    @Override
    public void addMenuOption(long groupId, String name, int extraPrice, int displayOrder) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("옵션명은 비어 있을 수 없습니다.");
        }
        MenuOption option = new MenuOption(groupId, name.trim(), extraPrice, displayOrder);
        menuOptionRepository.save(option);
    }

    @Override
    public void updateMenuOption(long optionId, String name, int extraPrice, int displayOrder) {
        MenuOption option = menuOptionRepository.findById(optionId);
        if (option == null) {
            throw new NotFoundException("수정할 옵션이 존재하지 않습니다.");
        }
        option.setOptionName(name);
        option.setExtraPrice(extraPrice);
        option.setDisplayOrder(displayOrder);
        menuOptionRepository.update(option);
    }

    @Override
    public void deleteMenuOption(long optionId) {
        menuOptionRepository.delete(optionId);
    }

    // --- 회원 관리 ---
    public List<Member> getMemberList() {
        return memberRepository.getAllMembers();
    }

    public void deleteMember(long id) {
        if (!memberRepository.deleteMember(id)) {
            throw new NotFoundException("삭제할 회원이 없습니다.");
        }
    }

    // --- 주문 관리 ---
    public List<Order> getOrderList() {
        return orderRepository.getAllOrders();
    }

    public void cancelOrder(long orderId) {
        if (!orderRepository.cancelOrder(orderId)) {
            throw new NotFoundException("취소할 수 있는 주문이 없습니다.");
        }
    }

    // --- 통계 기능 ---
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

    public Map<String, Integer> getSalesByPeriod(String format) {
        return orderRepository.getSalesByPeriod(format);
    }
}
