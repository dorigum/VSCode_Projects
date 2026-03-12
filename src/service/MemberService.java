package service;

import model.Member;
import model.OrderItem;

import java.util.List;

public interface MemberService {
	// 회원
	Member login(String phone, String password);

	boolean register(String phone, String password, int age);

	void showOrderHistory(Member member);

	// 찜
	void showWishlist(Member member);

	void addWishlist(Member member, long menuId);

	void removeWishlist(long wishlistId);

	// 퀵오더
	void showQuickOrder(Member member);

}