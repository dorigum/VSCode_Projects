package controller;

import java.util.List;

import exception.CafeKioskException;
import exception.ValidationException;
import model.Member;
import model.Order;
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
		}
	}

	public void showOrderHistory(Member member) {
		try {
			List<Order> orders = memberService.getOrderHistory(member);
			EndView.printOrderHistory(member, orders);
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public void showWishlist(Member member) {
		try {
			EndView.printWishlist(member, memberService.getWishlist(member));
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public void addWishlist(Member member, long menuId) {
		try {
			memberService.addWishlist(member, menuId);
			EndView.success("찜 목록에 추가되었습니다.");
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public void removeWishlist(long wishlistId) {
		try {
			memberService.removeWishlist(wishlistId);
			EndView.success("찜이 삭제되었습니다.");
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}

	public void showQuickOrder(Member member) {
		try {
			EndView.printQuickOrder(member, memberService.getQuickOrder(member));
		} catch (CafeKioskException e) {
			FailView.fail(e.getMessage());
		}
	}
}