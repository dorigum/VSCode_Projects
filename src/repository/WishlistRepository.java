package repository;

import exception.RepositoryException;
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
			throw new RepositoryException("찜 추가 중 오류가 발생했습니다.", e);
		}
	}

	// 회원별 찜 목록 조회
	public List<Wishlist> getWishlistByMember(long memberId) {
		List<Wishlist> list = new ArrayList<>();
		String sql = "SELECT w.*, m.menu_name, m.price " + "FROM WISHLIST w " + "JOIN MENU m ON w.menu_id = m.menu_id "
				+ "WHERE w.member_id = ? ORDER BY w.created_at DESC";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(new Wishlist(rs.getLong("wishlist_id"), rs.getLong("member_id"), rs.getLong("menu_id"),
							rs.getTimestamp("created_at"), rs.getString("menu_name"), // 추가!
							rs.getInt("price") // 추가!
					));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new RepositoryException("찜 조회 중 오류가 발생했습니다.", e);
		}
	}

	// 찜 삭제
	public boolean removeWishlist(long wishlistId) {
		String sql = "DELETE FROM WISHLIST WHERE wishlist_id = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, wishlistId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RepositoryException("찜 삭제 중 오류가 발생했습니다.", e);
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
			return false;
		} catch (SQLException e) {
			throw new RepositoryException("찜 중복 확인 중 오류가 발생했습니다.", e);
		}
	}
}
