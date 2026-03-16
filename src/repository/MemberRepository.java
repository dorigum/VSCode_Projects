package repository;

import model.Member;
import model.Order;
import model.PointHistory;

import java.util.List;

public interface MemberRepository {
	Member login(String phone, String password);

	List<Order> getOrderHistory(long memberId);

	List<PointHistory> getPointHistory(long memberId);

	List<Member> getAllMembers();

	Member getMemberById(long memberId);

	boolean deleteMember(long memberId);

	boolean updatePoint(long memberId, int amount);

	void savePointHistory(long memberId, int amount, String reason);

	boolean updateRole(long memberId, String newRole);

	boolean isPhoneExists(String phone);

	boolean register(Member member);

	void updatePreferredCategory(long memberId, int categoryId);
}
