package controller;

import java.util.List;

import exception.CafeKioskException;
import exception.ValidationException;
import model.Member;
import model.Order;
import model.OrderItem;
import model.PointHistory;
import service.MemberService;
import view.EndView;
import view.FailView;
import model.Menu;
import java.util.List;

public class MemberController {
	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		if (memberService == null) {
			throw new ValidationException("MemberService는 null일 수 없습니다.");
		}
		this.memberService = memberService;
	}

	public void register(String phone, String password, int age) {
		try {
			memberService.register(phone, password, age);
			EndView.success("회원가입이 완료되었습니다!");
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public Member login(String phone, String password) {
		try {
			Member member = memberService.login(phone, password);
			EndView.printLoginSuccess(member);
			return member;
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
			return null;
		} catch (Exception e) {
			// 기타 예상치 못한 오류 발생 시 사용자에게 노출하지 않고 로그만 남김
			System.err.println("시스템 오류: " + e.getMessage());
			return null;
		}
	}

	public void showOrderHistory(Member member) {
		try {
			List<Order> orders = memberService.getOrderHistory(member);
			EndView.printOrders(orders);
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public void showPointHistory(Member member) {
		try {
			List<PointHistory> history = memberService.getPointHistory(member);
			EndView.printPointHistory(member, history);
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public List<OrderItem> showQuickOrder(Member member) {
		try {
			Order quickOrder = memberService.getQuickOrder(member);
			EndView.printQuickOrder(member, quickOrder);

			if (quickOrder == null || quickOrder.getItems() == null || quickOrder.getItems().isEmpty()) {
				return null;
			}

			System.out.print("이 주문으로 바로 주문하시겠습니까? (Y/N): ");
			String input = new java.util.Scanner(System.in).nextLine().trim();
			if ("Y".equalsIgnoreCase(input)) {
				return quickOrder.getItems();
			}
			return null;

		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
			return null;
		}
	}

	public void register(String phone, String password, int age, int preferredCategoryId) {
		try {
			memberService.register(phone, password, age, preferredCategoryId);
			EndView.success("회원가입이 완료되었습니다!");
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public List<Menu> getRecommendedMenus(Member member) {
		try {
			return memberService.getRecommendedMenus(member.getPreferredCategoryId());
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
			return null;
		}
	}

	public void updatePreferredCategory(Member member, int categoryId) {
		try {
			memberService.updatePreferredCategory(member.getMemberId(), categoryId);
			member.setPreferredCategoryId(categoryId);
			EndView.success("선호 카테고리가 변경되었습니다!");
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}
}