package repository;

import model.Member;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

    // 1. 로그인 확인 (회원번호와 비밀번호로 조회)
    public Member login(long memberId, String password) {
        String sql = "SELECT * FROM MEMBER WHERE member_id = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, memberId);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Member(
                        rs.getLong("member_id"),
                        rs.getString("password"),
                        rs.getInt("age"),
                        rs.getInt("point_balance"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("로그인 처리 중 오류: " + e.getMessage());
        }
        return null;
    }

    // 2. 특정 회원의 주문 내역 상세 조회
    public List<OrderItem> getOrderHistory(long memberId) {
        List<OrderItem> historyList = new ArrayList<>();
        String sql = "SELECT oi.* FROM ORDER_ITEM oi " +
                     "JOIN ORDERS o ON oi.order_id = o.order_id " +
                     "WHERE o.member_id = ? " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(new OrderItem(
                        rs.getLong("order_item_id"),
                        rs.getLong("order_id"),
                        rs.getLong("menu_id"),
                        rs.getInt("quantity"),
                        rs.getInt("unit_price"),
                        rs.getString("menu_name_snapshot"),
                        rs.getString("category_name_snapshot")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("주문 내역 조회 중 오류: " + e.getMessage());
        }
        return historyList;
    }

    // 3. 모든 회원 조회
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM MEMBER ORDER BY member_id DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(new Member(
                    rs.getLong("member_id"),
                    rs.getString("password"),
                    rs.getInt("age"),
                    rs.getInt("point_balance"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    // 4. 회원 삭제
    public void deleteMember(long memberId) {
        String sql = "DELETE FROM MEMBER WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
