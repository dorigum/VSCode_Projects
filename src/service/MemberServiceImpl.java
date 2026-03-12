package service;

import model.Member;
import model.OrderItem;
import model.Wishlist;
import repository.MemberRepositoryImpl;
import repository.WishlistRepository;
import repository.QuickOrderRepository;
import java.util.List;

public class MemberServiceImpl implements MemberService {
	private MemberRepositoryImpl memberRepository = new MemberRepositoryImpl();
	private WishlistRepository wishlistRepository = new WishlistRepository();
	private QuickOrderRepository quickOrderRepository = new QuickOrderRepository();

	// ─── 로그인 ───────────────────────────────────────
	@Override
	public Member login(String phone, String password) {
		Member member = memberRepository.login(phone, password);
		if (member != null) {
			System.out.println("로그인 성공! 환영합니다, " + member.getPhone() + "님.");
			System.out.printf("보유 포인트: %,d원%n", member.getPointBalance());
			return member;
		} else {
			System.out.println("로그인 실패: 전화번호 또는 비밀번호를 확인하세요.");
			return null;
		}
	}

	// ─── 회원가입 ──────────────────────────────────────
	@Override
	public boolean register(String phone, String password, int age) {
		// 유효성 검사
		if (!phone.matches("^010-\\d{4}-\\d{4}$")) {
			System.out.println("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
			return false;
		}
		if (password.length() < 4) {
			System.out.println("비밀번호는 4자리 이상이어야 합니다.");
			return false;
		}
		if (memberRepository.isPhoneExists(phone)) {
			System.out.println("이미 가입된 전화번호입니다.");
			return false;
		}
		boolean result = memberRepository.register(new Member(phone, password, age));
		if (result)
			System.out.println("회원가입이 완료되었습니다!");
		return result;
	}

	// ─── 주문 내역 ─────────────────────────────────────
	@Override
	public void showOrderHistory(Member member) {
		if (member == null)
			return;
		List<OrderItem> history = memberRepository.getOrderHistory(member.getMemberId());
		System.out.println("\n===== " + member.getPhone() + "님의 주문 내역 =====");
		if (history.isEmpty()) {
			System.out.println("주문 내역이 없습니다.");
		} else {
			for (OrderItem item : history) {
				System.out.println(item);
			}
		}
	}

	// ─── 찜 목록 조회 ──────────────────────────────────
	@Override
	public void showWishlist(Member member) {
		if (member == null)
			return;
		List<Wishlist> list = wishlistRepository.getWishlistByMember(member.getMemberId());
		System.out.println("\n===== " + member.getPhone() + "님의 찜 목록 =====");
		if (list.isEmpty()) {
			System.out.println("찜한 메뉴가 없습니다.");
		} else {
			list.forEach(System.out::println);
		}
	}

	// ─── 찜 추가 ───────────────────────────────────────
	@Override
	public void addWishlist(Member member, long menuId) {
		if (member == null)
			return;
		if (wishlistRepository.isAlreadyWished(member.getMemberId(), menuId)) {
			System.out.println("이미 찜한 메뉴입니다.");
			return;
		}
		boolean result = wishlistRepository.addWishlist(member.getMemberId(), menuId);
		System.out.println(result ? "찜 목록에 추가되었습니다!" : "찜 추가에 실패했습니다.");
	}

	// ─── 찜 삭제 ───────────────────────────────────────
	@Override
	public void removeWishlist(long wishlistId) {
		boolean result = wishlistRepository.removeWishlist(wishlistId);
		System.out.println(result ? "찜 목록에서 삭제되었습니다." : "삭제에 실패했습니다.");
	}

	public void showQuickOrder(Member member) {
		if (member == null)
			return;

		List<OrderItem> lastItems = quickOrderRepository.getLastOrderItems(member.getMemberId());

		if (lastItems.isEmpty()) {
			System.out.println("이전 주문 내역이 없어 퀵오더를 사용할 수 없습니다.");
			return;
		}

		System.out.println("\n===== 퀵오더 - 최근 주문 내역 =====");
		int total = 0;
		for (OrderItem item : lastItems) {
			System.out.printf("- %-15s %d개 x %,d원%n", item.getMenuNameSnapshot(), item.getQuantity(),
					item.getUnitPrice());
			total += item.getQuantity() * item.getUnitPrice();
		}
		System.out.printf("합계: %,d원%n", total);
		System.out.println("위 주문을 바로 주문하시겠습니까? (1. 예 / 0. 아니오)");
	}

}