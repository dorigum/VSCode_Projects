package service;

import exception.BusinessRuleException;
import exception.NotFoundException;
import exception.RepositoryException;
import exception.ValidationException;
import model.*;
import repository.*;

import java.util.LinkedHashMap;
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
		this(new MenuRepositoryImpl(), new MemberRepositoryImpl(), new CategoryRepositoryImpl(),
				new OrderRepositoryImpl(), new OptionGroupRepositoryImpl(), new MenuOptionRepositoryImpl());
	}

	public AdminServiceImpl(MenuRepository menuRepository, MemberRepository memberRepository,
			CategoryRepository categoryRepository, OrderRepository orderRepository,
			OptionGroupRepository optionGroupRepository, MenuOptionRepository menuOptionRepository) {
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

		String trimmedName = name.trim();
		Menu menu = new Menu(categoryId, trimmedName, price, description == null ? "" : description.trim());

		// 1. 메뉴 기본 정보 등록
		if (!menuRepository.addMenu(menu)) {
			throw new BusinessRuleException("메뉴 등록에 실패했습니다.");
		}

		// 2. [지능형 옵션 매핑] '프라푸치노' 또는 '라떼' 키워드 검사
		if (trimmedName.contains("프라푸치노") || trimmedName.contains("라떼")) {
			List<OptionGroup> allGroups = optionGroupRepository.findAll();
			
			// '휘핑유무' 및 '사이즈' 옵션 그룹 찾기
			OptionGroup whippingGroup = allGroups.stream().filter(g -> g.getGroupName().contains("휘핑")).findFirst().orElse(null);
			OptionGroup sizeGroup = allGroups.stream().filter(g -> g.getGroupName().contains("사이즈")).findFirst().orElse(null);

			if (whippingGroup != null || sizeGroup != null) {
				// 방금 등록된 메뉴의 ID 조회
				List<Menu> menus = menuRepository.getAllMenus();
				Menu registeredMenu = menus.stream().filter(m -> m.getMenuName().equals(trimmedName))
						.sorted((m1, m2) -> Long.compare(m2.getMenuId(), m1.getMenuId())).findFirst().orElse(null);

				if (registeredMenu != null) {
					// 사이즈 옵션 먼저 추가 (표시 순서 고려)
					if (sizeGroup != null) {
						menuRepository.addOptionGroupToMenu(registeredMenu.getMenuId(), sizeGroup.getGroupId(), 1);
					}
					// 휘핑 옵션 추가
					if (whippingGroup != null) {
						menuRepository.addOptionGroupToMenu(registeredMenu.getMenuId(), whippingGroup.getGroupId(), 2);
					}
				}
			}
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
		Member member = memberRepository.getMemberById(id);
		if (member == null) {
			throw new NotFoundException("삭제할 회원이 없습니다.");
		}

		if ("ADMIN".equalsIgnoreCase(member.getRole())) {
			throw new BusinessRuleException("관리자 계정은 직접 삭제할 수 없습니다. 삭제하려면 먼저 일반 회원으로 등급을 변경해야 합니다.");
		}

		if (!memberRepository.deleteMember(id)) {
			throw new RepositoryException("회원 삭제 중 알 수 없는 오류가 발생했습니다.");
		}
	}

	@Override
	public void updateMemberRole(long id, String newRole) {
		if (newRole == null || (!newRole.equalsIgnoreCase("ADMIN") && !newRole.equalsIgnoreCase("USER"))) {
			throw new ValidationException("올바른 등급(ADMIN, USER)을 입력해 주세요.");
		}

		Member targetMember = memberRepository.getMemberById(id);
		if (targetMember == null) {
			throw new NotFoundException("해당 회원을 찾을 수 없습니다.");
		}

		// 1인 관리자 제한 체크: 새 등급을 ADMIN으로 하려는 경우
		if (newRole.equalsIgnoreCase("ADMIN")) {
			List<Member> allMembers = memberRepository.getAllMembers();
			boolean adminExists = allMembers.stream().anyMatch(m -> "ADMIN".equalsIgnoreCase(m.getRole()) && m.getMemberId() != id);
			
			if (adminExists) {
				throw new BusinessRuleException("이미 관리자가 존재합니다. 관리자는 1명만 설정할 수 있습니다.");
			}
		}

		if (!memberRepository.updateRole(id, newRole)) {
			throw new RepositoryException("등급 변경 중 오류가 발생했습니다.");
		}
	}

	@Override
	public void updateMemberPoint(long id, int amount, String reason) {
		if (amount == 0) {
			throw new ValidationException("수정할 포인트 금액은 0원일 수 없습니다.");
		}
		
		if (reason == null || reason.trim().isEmpty()) {
			throw new ValidationException("포인트 수정 사유를 반드시 입력해 주세요.");
		}

		Member member = memberRepository.getMemberById(id);
		if (member == null) {
			throw new NotFoundException("포인트를 수정할 회원을 찾을 수 없습니다.");
		}

		if ("ADMIN".equalsIgnoreCase(member.getRole())) {
			throw new BusinessRuleException("관리자 등급의 회원은 포인트를 수정할 수 없습니다.");
		}

		if (!memberRepository.updatePoint(id, amount)) {
			throw new RepositoryException("포인트 수정 중 알 수 없는 오류가 발생했습니다.");
		}
		
		// [신규] 포인트 변동 히스토리 저장
		memberRepository.savePointHistory(id, amount, reason);
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

	@Override
	public Map<String, Object> getSalesStatsByPeriod(String startDate, String endDate) {
		return orderRepository.getSalesStatsByPeriod(startDate, endDate);
	}

	@Override
	public Map<Integer, Integer> getHourlySales() {
		return orderRepository.getHourlySales();
	}

	@Override
	public Map<String, Integer> getDayOfWeekSales() {
		Map<String, Integer> rawStats = orderRepository.getDayOfWeekSales();
		Map<String, Integer> koreanStats = new LinkedHashMap<>();
		
		// 영문 요일을 한글 요일로 매핑하여 정렬된 순서로 저장
		String[][] mapping = {
			{"Monday", "월요일"}, {"Tuesday", "화요일"}, {"Wednesday", "수요일"}, 
			{"Thursday", "목요일"}, {"Friday", "금요일"}, {"Saturday", "토요일"}, {"Sunday", "일요일"}
		};

		for (String[] pair : mapping) {
			koreanStats.put(pair[1], rawStats.getOrDefault(pair[0], 0));
		}
		return koreanStats;
	}

	@Override
	public List<Map<String, Object>> getTopSpenders(int limit) {
		return orderRepository.getTopSpenders(limit);
	}

	@Override
	public void exportStatisticsToCSV() {
		String dirPath = "resources/cafe_sales_report";
		java.io.File dir = new java.io.File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs(); // 폴더가 없으면 생성
		}

		String fileName = dirPath + "/cafe_sales_report_" + System.currentTimeMillis() + ".csv";
		try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(fileName), "MS949")) {
			// 1. 헤더 및 요약
			writer.println("--- 카페 키오스크 매출 통계 보고서 ---");
			writer.println("생성일시," + new java.util.Date());
			writer.println("총 누적 매출," + getTotalSales() + "원");
			writer.println();

			// 2. 일별 매출 추이
			writer.println("[일별 매출 추이]");
			writer.println("날짜,매출액");
			getDailySales().forEach((date, sales) -> writer.println(date + "," + sales));
			writer.println();

			// 3. 카테고리별 매출
			writer.println("[카테고리별 매출 분석]");
			writer.println("카테고리,매출액");
			getSalesByCategory().forEach((cat, sales) -> writer.println(cat + "," + sales));
			writer.println();

			// 4. 시간대별 매출
			writer.println("[시간대별 매출 분석]");
			writer.println("시간,매출액");
			getHourlySales().forEach((hour, sales) -> writer.println(hour + "시," + sales));
			writer.println();

			// 5. 요일별 매출
			writer.println("[요일별 매출 분석]");
			writer.println("요일,매출액");
			getDayOfWeekSales().forEach((day, sales) -> writer.println(day + "," + sales));
			writer.println();

			// 6. 우수 회원 Top 5
			writer.println("[우수 회원 기여도 분석]");
			writer.println("순위,휴대폰 번호,누적 결제액");
			List<Map<String, Object>> topMembers = getTopSpenders(5);
			for (int i = 0; i < topMembers.size(); i++) {
				Map<String, Object> m = topMembers.get(i);
				writer.println((i + 1) + "위," + m.get("phone") + "," + m.get("total"));
			}

			System.out.println("\n[성공] 매출 보고서가 생성되었습니다: " + fileName);
		} catch (java.io.IOException e) {
			throw new exception.InfrastructureException("CSV 파일 내보내기 중 오류가 발생했습니다.", e);
		}
	}
}