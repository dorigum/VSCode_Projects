package repository;

import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    // 상품 등록 (DB: INSERT INTO MENU ...)
    public void addMenu(Menu menu) {
        String sql = "INSERT INTO MENU (category_id, menu_name, price, description, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, menu.getCategoryId());
            pstmt.setString(2, menu.getMenuName());
            pstmt.setInt(3, menu.getPrice());
            pstmt.setString(4, menu.getDescription() == null ? "" : menu.getDescription());
            pstmt.setInt(5, menu.isAvailable() ? 1 : 0);
            
            pstmt.executeUpdate();
            System.out.println("DEBUG: DB에 상품이 저장되었습니다.");
        } catch (SQLException e) {
            System.err.println("상품 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 모든 상품 목록 조회 (DB: SELECT * FROM MENU)
    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM MENU ORDER BY menu_id DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                menus.add(new Menu(
                    rs.getLong("menu_id"),
                    rs.getInt("category_id"),
                    rs.getString("menu_name"),
                    rs.getInt("price"),
                    rs.getString("description"),
                    rs.getInt("is_available") == 1,
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("상품 목록 조회 중 오류 발생: " + e.getMessage());
        }
        return menus;
    }

    // ID로 상품 찾기 (DB: SELECT ... WHERE menu_id = ?)
    public Menu findById(long id) {
        String sql = "SELECT * FROM MENU WHERE menu_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Menu(
                        rs.getLong("menu_id"),
                        rs.getInt("category_id"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("description"),
                        rs.getInt("is_available") == 1,
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 상품 삭제 (DB: DELETE FROM MENU WHERE menu_id = ?)
    public void deleteMenu(long id) {
        String sql = "DELETE FROM MENU WHERE menu_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
