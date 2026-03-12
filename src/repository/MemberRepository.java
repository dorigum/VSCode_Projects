package repository;

import model.Member;
import model.OrderItem;

import java.util.List;

public interface MemberRepository {
	Member login(String phone, String password);

	List<OrderItem> getOrderHistory(long memberId);

	List<Member> getAllMembers();

	boolean deleteMember(long memberId);

	boolean isPhoneExists(String phone);

	boolean register(Member member);

}
