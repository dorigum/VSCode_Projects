package controller;

import java.util.List;
import java.util.Scanner;

import exception.CafeKioskException;
import exception.ValidationException;
import model.Member;
import model.Order;
import model.PointHistory;
import model.OrderItem;
import model.Menu;
import service.MemberService;
import view.EndView;
import view.FailView;

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

	public void showWishlist(Member member) {
		try {
			List<model.Wishlist> wishlists = memberService.getWishlist(member);
			EndView.printWishlist(member, wishlists);
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
			String input = new Scanner(System.in).nextLine().trim();
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
