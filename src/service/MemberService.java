package service;

import model.Member;
import model.Order;
import model.OrderItem;
import model.Wishlist;

import java.util.List;

public interface MemberService {
	Member login(String phone, String password);

	boolean register(String phone, String password, int age);

	List<Order> getOrderHistory(Member member);

	List<Wishlist> getWishlist(Member member);

	void addWishlist(Member member, long menuId);

	void removeWishlist(long wishlistId);

	List<OrderItem> getQuickOrder(Member member);

}
