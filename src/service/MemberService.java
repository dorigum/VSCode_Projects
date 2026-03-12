package service;

import model.Member;
import model.OrderItem;

import java.util.List;

public interface MemberService {
	Member login(String phone, String password); // long → String 변경!

	void showOrderHistory(Member member);

	boolean register(String phone, String password, int age); // 추가!
}