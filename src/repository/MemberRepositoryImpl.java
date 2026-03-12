package repository;

import model.Member;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepository {

	// 로그인 - phone으로
	public Member login(String phone, String password) {
		String sql = "SELECT * FROM MEMBER WHERE phone = ? AND password = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Member(rs.getLong("member_id"), rs.getString("phone"), rs.getString("password"),
							rs.getInt("age"), rs.getInt("point_balance"), rs.getString("role"),
							rs.getTimestamp("created_at"));
				}
			}
		} catch (SQLException e) {
			System.err.println("로그인 처리 중 오류: " + e.getMessage());
		}
		return null;
	}

	// 회원가입
	public boolean register(Member member) {
		String sql = "INSERT INTO MEMBER (phone, password, age) VALUES (?, ?, ?)";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, member.getPhone());
			pstmt.setString(2, member.getPassword());
			pstmt.setInt(3, member.getAge());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("회원가입 실패: " + e.getMessage());
			return false;
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
			e.printStackTrace();
		}
		return false;
	}

	// 주문 내역 조회
	public List<OrderItem> getOrderHistory(long memberId) {
		List<OrderItem> historyList = new ArrayList<>();
		String sql = "SELECT oi.* FROM ORDER_ITEM oi " + "JOIN ORDERS o ON oi.order_id = o.order_id "
				+ "WHERE o.member_id = ? ORDER BY o.order_date DESC";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					historyList.add(new OrderItem(rs.getLong("order_item_id"), rs.getLong("order_id"),
							rs.getLong("menu_id"), rs.getInt("quantity"), rs.getInt("unit_price"),
							rs.getString("menu_name_snapshot"), rs.getString("category_name_snapshot")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return historyList;
	}

	// 전체 회원 조회
	public List<Member> getAllMembers() {
		List<Member> members = new ArrayList<>();
		String sql = "SELECT * FROM MEMBER ORDER BY member_id DESC";
		try (Connection conn = DBUtil.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				members.add(new Member(rs.getLong("member_id"), rs.getString("phone"), rs.getString("password"),
						rs.getInt("age"), rs.getInt("point_balance"), rs.getString("role"),
						rs.getTimestamp("created_at")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return members;
	}

	// 회원 삭제
	public void deleteMember(long memberId) {
		String sql = "DELETE FROM MEMBER WHERE member_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}