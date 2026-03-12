package repository;

import model.Wishlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistRepository {

	// 찜 추가
	public boolean addWishlist(long memberId, long menuId) {
		String sql = "INSERT INTO WISHLIST (member_id, menu_id) VALUES (?, ?)";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			pstmt.setLong(2, menuId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("찜 추가 실패: " + e.getMessage());
			return false;
		}
	}

	// 회원별 찜 목록 조회
	public List<Wishlist> getWishlistByMember(long memberId) {
		List<Wishlist> list = new ArrayList<>();
		String sql = "SELECT * FROM WISHLIST WHERE member_id = ? ORDER BY created_at DESC";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(new Wishlist(rs.getLong("wishlist_id"), rs.getLong("member_id"), rs.getLong("menu_id"),
							rs.getTimestamp("created_at")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 찜 삭제
	public boolean removeWishlist(long wishlistId) {
		String sql = "DELETE FROM WISHLIST WHERE wishlist_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, wishlistId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 중복 찜 체크
	public boolean isAlreadyWished(long memberId, long menuId) {
		String sql = "SELECT COUNT(*) FROM WISHLIST WHERE member_id = ? AND menu_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			pstmt.setLong(2, menuId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}