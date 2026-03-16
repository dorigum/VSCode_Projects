package repository;

import exception.RepositoryException;
import model.Member;
import model.Order;
import model.OrderItem;
import model.PointHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepository {

	@Override
	public void savePointHistory(long memberId, int amount, String reason) {
		String checkSql = "CREATE TABLE IF NOT EXISTS POINT_HISTORY (" + "history_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "member_id BIGINT NOT NULL, " + "amount INT NOT NULL, " + "reason VARCHAR(255) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "FOREIGN KEY (member_id) REFERENCES MEMBER(member_id) ON DELETE CASCADE)";

		String insertSql = "INSERT INTO POINT_HISTORY (member_id, amount, reason) VALUES (?, ?, ?)";

		try (Connection conn = DBUtil.getConnection();
				Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

			// 1. 테이블 존재 여부 보장 (안정성)
			stmt.execute(checkSql);

			// 2. 히스토리 저장
			pstmt.setLong(1, memberId);
			pstmt.setInt(2, amount);
			pstmt.setString(3, reason);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new RepositoryException("포인트 내역 저장 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	public List<PointHistory> getPointHistory(long memberId) {
		List<PointHistory> historyList = new ArrayList<>();
		String sql = "SELECT * FROM POINT_HISTORY WHERE member_id = ? ORDER BY created_at DESC";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					historyList.add(new PointHistory(rs.getInt("history_id"), rs.getLong("member_id"),
							rs.getInt("amount"), rs.getString("reason"), rs.getTimestamp("created_at")));
				}
			}
			return historyList;
		} catch (SQLException e) {
			// 테이블이 아직 없는 경우 빈 목록 반환
			return historyList;
		}
	}

	// 로그인 - phone으로
	public Member login(String phone, String password) {
		String sql = "SELECT * FROM MEMBER WHERE phone = ? AND password = BINARY ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Member member = new Member(rs.getLong("member_id"), rs.getString("phone"), rs.getString("password"),
							rs.getInt("age"), rs.getInt("point_balance"), rs.getString("role"),
							rs.getTimestamp("created_at"));
					member.setPreferredCategoryId(rs.getInt("preferred_category_id")); // ← 추가
					return member;
				}
			}
		} catch (SQLException e) {
			throw new RepositoryException("로그인 처리 중 오류가 발생했습니다.", e);
		}
		return null;
	}

	// 회원가입
	public boolean register(Member member) {
		String sql = "INSERT INTO MEMBER (phone, password, age, preferred_category_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, member.getPhone());
			pstmt.setString(2, member.getPassword());
			pstmt.setInt(3, member.getAge());
			pstmt.setInt(4, member.getPreferredCategoryId());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RepositoryException("회원가입 처리 중 오류가 발생했습니다.", e);
		}
	}

	// 전화번호 중복 확인
	public boolean isPhoneExists(String phone) {
		String sql = "SELECT COUNT(*) FROM MEMBER WHERE phone = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new RepositoryException("전화번호 조회 중 오류가 발생했습니다.", e);
		}
		return false;
	}

	// 주문 내역 조회
	public List<Order> getOrderHistory(long memberId) {
		List<Order> orderList = new ArrayList<>();

		// 1단계: ORDERS 먼저 조회
		String orderSql = "SELECT * FROM ORDERS WHERE member_id = ? ORDER BY order_date DESC";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Order order = new Order(rs.getLong("order_id"), rs.getLong("member_id"), rs.getInt("total_amount"),
							rs.getInt("point_used"), rs.getInt("point_earned"), rs.getString("status"),
							rs.getTimestamp("order_date"));

					// 2단계: 각 Order의 OrderItem 조회
					String itemSql = "SELECT * FROM ORDER_ITEM WHERE order_id = ?";
					try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
						itemPs.setLong(1, order.getOrderId());
						List<OrderItem> items = new ArrayList<>();
						try (ResultSet itemRs = itemPs.executeQuery()) {
							while (itemRs.next()) {
								items.add(new OrderItem(itemRs.getLong("order_item_id"), itemRs.getLong("order_id"),
										itemRs.getLong("menu_id"), itemRs.getInt("quantity"),
										itemRs.getInt("unit_price"), itemRs.getString("menu_name_snapshot"),
										itemRs.getString("category_name_snapshot")));
							}
						}
						order.setItems(items);
					}
					orderList.add(order);
				}
			}
			return orderList;
		} catch (SQLException e) {
			throw new RepositoryException("주문 내역 조회 중 오류가 발생했습니다.", e);
		}
	}

	// 회원 ID로 조회
	@Override
	public Member getMemberById(long memberId) {
		String sql = "SELECT * FROM MEMBER WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Member(rs.getLong("member_id"), rs.getString("phone"), rs.getString("password"),
							rs.getInt("age"), rs.getInt("point_balance"), rs.getString("role"),
							rs.getTimestamp("created_at"));
				}
			}
		} catch (SQLException e) {
			throw new RepositoryException("회원 조회 중 오류가 발생했습니다.", e);
		}
		return null;
	}

	// 전체 회원 조회
	@Override
	public List<Member> getAllMembers() {
		List<Member> members = new ArrayList<>();
		String sql = "SELECT * FROM MEMBER ORDER BY member_id DESC";
		try (Connection conn = DBUtil.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Member m = new Member(rs.getLong("member_id"), rs.getString("phone"), rs.getString("password"),
						rs.getInt("age"), rs.getInt("point_balance"), rs.getString("role"),
						rs.getTimestamp("created_at"));
				m.setPreferredCategoryId(rs.getInt("preferred_category_id")); // ← 추가
				members.add(m);
			}
			return members;
		} catch (SQLException e) {
			throw new RepositoryException("회원 목록 조회 중 오류가 발생했습니다.", e);
		}
	}

	// 회원 삭제
	public boolean deleteMember(long memberId) {
		String sql = "DELETE FROM MEMBER WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RepositoryException("회원 삭제 중 오류가 발생했습니다.", e);
		}
	}

	public void updatePreferredCategory(long memberId, int categoryId) {
		String sql = "UPDATE MEMBER SET preferred_category_id = ? WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, categoryId);
			pstmt.setLong(2, memberId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException("선호 카테고리 수정 중 오류가 발생했습니다.", e);
		}
	}

	// 포인트 수정 (증감)
	@Override
	public boolean updatePoint(long memberId, int amount) {
		String sql = "UPDATE MEMBER SET point_balance = point_balance + ? WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, amount);
			pstmt.setLong(2, memberId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RepositoryException("포인트 수정 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	public boolean updateRole(long memberId, String newRole) {
		String sql = "UPDATE MEMBER SET role = ? WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newRole.toUpperCase());
			pstmt.setLong(2, memberId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RepositoryException("등급 변경 중 오류가 발생했습니다.", e);
		}
	}
}
