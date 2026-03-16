package service;

import model.Member;
import model.Menu;
import model.Order;
import model.OrderItem;
import model.PointHistory;
import model.Wishlist;

import java.util.List;

public interface MemberService {
	Member login(String phone, String password);

	boolean register(String phone, String password, int age);

	List<Order> getOrderHistory(Member member);

	List<PointHistory> getPointHistory(Member member);

	List<Wishlist> getWishlist(Member member);

	void addWishlist(Member member, long menuId);

	void removeWishlist(long wishlistId);

	Order getQuickOrder(Member member);

	List<Menu> getRecommendedMenus(int categoryId); // ← 추가

	void updatePreferredCategory(long memberId, int categoryId); // ← 추가

	boolean register(String phone, String password, int age, int preferredCategoryId);

	/**
	 * 포인트 잔액을 업데이트하고 히스토리를 저장합니다.
	 * 
	 * @param memberId 회원 ID
	 * @param amount   변동 금액 (양수: 적립, 음수: 사용)
	 * @param reason   변동 사유
	 */
	void updatePoint(long memberId, int amount, String reason);

}
