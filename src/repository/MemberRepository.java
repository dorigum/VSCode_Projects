package repository;

import model.Member;
import model.Order;

import java.util.List;

public interface MemberRepository {
	Member login(String phone, String password);

	List<Order> getOrderHistory(long memberId);

	List<Member> getAllMembers();

	boolean deleteMember(long memberId);

	boolean isPhoneExists(String phone);

	boolean register(Member member);

}
