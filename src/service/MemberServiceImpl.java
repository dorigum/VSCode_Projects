package service;

import exception.BusinessRuleException;
import exception.ConflictException;
import exception.ValidationException;
import model.Member;
import model.OrderItem;
import model.PointHistory;
import model.Wishlist;
import repository.MemberRepository;
import repository.MemberRepositoryImpl;
import repository.WishlistRepository;
import repository.QuickOrderRepository;
import java.util.List;
import model.Order;
import model.Menu;
import repository.MenuRepository;
import repository.MenuRepositoryImpl;
import java.util.ArrayList;

public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;
	private final WishlistRepository wishlistRepository;
	private final QuickOrderRepository quickOrderRepository;
	private final MenuRepository menuRepository;

	public MemberServiceImpl() {
		this(new MemberRepositoryImpl(), new WishlistRepository(), new QuickOrderRepository(),
				new MenuRepositoryImpl());
	}

	public MemberServiceImpl(MemberRepository memberRepository, WishlistRepository wishlistRepository,
			QuickOrderRepository quickOrderRepository, MenuRepository menuRepository) {
		if (memberRepository == null) {
			throw new ValidationException("MemberRepository는 null일 수 없습니다.");
		}
		if (wishlistRepository == null) {
			throw new ValidationException("WishlistRepository는 null일 수 없습니다.");
		}
		if (quickOrderRepository == null) {
			throw new ValidationException("QuickOrderRepository는 null일 수 없습니다.");
		}
		this.memberRepository = memberRepository;
		this.wishlistRepository = wishlistRepository;
		this.quickOrderRepository = quickOrderRepository;
		this.menuRepository = menuRepository;
	}

	@Override
	public Member login(String phone, String password) {
		if (phone == null || phone.trim().isEmpty()) {
			throw new ValidationException("전화번호는 비어 있을 수 없습니다.");
		}
		if (password == null || password.trim().isEmpty()) {
			throw new ValidationException("비밀번호는 비어 있을 수 없습니다.");
		}

		// 전화번호 정규화: 하이픈 제거 후 숫자만 추출
		String digitsOnly = phone.replaceAll("[^0-9]", "");

		// 01012345678 형식을 010-1234-5678 형식으로 변환 (DB 저장 형식에 맞춤)
		String normalizedPhone = digitsOnly;
		if (digitsOnly.length() == 11) {
			normalizedPhone = digitsOnly.substring(0, 3) + "-" + digitsOnly.substring(3, 7) + "-"
					+ digitsOnly.substring(7);
		}

		Member member = memberRepository.login(normalizedPhone, password);
		if (member == null) {
			throw new BusinessRuleException("전화번호 또는 비밀번호가 일치하지 않습니다.");
		}
		return member;
	}

	@Override
	public boolean register(String phone, String password, int age) {
		if (phone == null || !phone.matches("^010-\\d{4}-\\d{4}$")) {
			throw new ValidationException("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
		}
		if (password == null || password.length() < 4) {
			throw new ValidationException("비밀번호는 4자리 이상이어야 합니다.");
		}
		if (memberRepository.isPhoneExists(phone)) {
			throw new ConflictException("이미 가입된 전화번호입니다.");
		}

		Member newMember = new Member(phone, password, age);
		boolean result = memberRepository.register(newMember);

		if (!result) {
			throw new BusinessRuleException("회원가입에 실패했습니다.");
		}

		// 회원가입 성공 후 보너스 포인트 지급 (예: 1000P)
		try {
			// 방금 가입한 회원의 ID를 가져오기 위해 다시 조회
			Member registered = memberRepository.login(phone, password);
			if (registered != null) {
				updatePoint(registered.getMemberId(), 1000, "회원가입 축하 포인트");
			}
		} catch (Exception e) {
			// 포인트 지급 실패가 회원가입 전체 실패로 이어지지는 않게 로그만 남기거나 무시 (필요시 수정)
			System.err.println("회원가입 보너스 포인트 지급 중 오류: " + e.getMessage());
		}

		return true;
	}

	@Override
	public void updatePoint(long memberId, int amount, String reason) {
		if (memberId <= 0) {
			throw new ValidationException("유효하지 않은 회원 ID입니다.");
		}

		// 1. 포인트 잔액 업데이트
		boolean updated = memberRepository.updatePoint(memberId, amount);
		if (!updated) {
			throw new BusinessRuleException("포인트 업데이트에 실패했습니다.");
		}

		// 2. 포인트 변동 내역 저장
		memberRepository.savePointHistory(memberId, amount, reason);
	}

	@Override
	public List<Order> getOrderHistory(Member member) {
		validateMember(member);
		return memberRepository.getOrderHistory(member.getMemberId());
	}

	@Override
	public List<PointHistory> getPointHistory(Member member) {
		validateMember(member);
		return memberRepository.getPointHistory(member.getMemberId());
	}

	@Override
	public Order getQuickOrder(Member member) {
		validateMember(member);
		return quickOrderRepository.getRecentOrder(member.getMemberId());
	}

	private void validateMember(Member member) {
		if (member == null) {
			throw new ValidationException("회원 정보가 유효하지 않습니다.");
		}
	}

	@Override
	public boolean register(String phone, String password, int age, int preferredCategoryId) {
		if (phone == null || !phone.matches("^010-\\d{4}-\\d{4}$")) {
			throw new ValidationException("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
		}
		if (password == null || password.length() < 4) {
			throw new ValidationException("비밀번호는 4자리 이상이어야 합니다.");
		}
		if (memberRepository.isPhoneExists(phone)) {
			throw new ConflictException("이미 가입된 전화번호입니다.");
		}
		Member member = new Member(phone, password, age);
		member.setPreferredCategoryId(preferredCategoryId);
		boolean result = memberRepository.register(member);
		if (!result) {
			throw new BusinessRuleException("회원가입에 실패했습니다.");
		}
		return true;
	}

	@Override
	public List<Menu> getRecommendedMenus(int categoryId) {
		if (categoryId <= 0)
			return new ArrayList<>();
		List<Menu> all = menuRepository.getMenusByCategoryId(categoryId);
		if (all.size() <= 3)
			return all;

		// 랜덤 3개 추출
		java.util.Collections.shuffle(all);
		return all.subList(0, 3);
	}

	@Override
	public void updatePreferredCategory(long memberId, int categoryId) {
		memberRepository.updatePreferredCategory(memberId, categoryId);
	}
}
