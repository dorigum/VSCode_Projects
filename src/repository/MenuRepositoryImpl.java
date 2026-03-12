package repository;

import exception.RepositoryException;
import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepositoryImpl implements MenuRepository {

    public boolean addMenu(Menu menu) {
        String sql = "INSERT INTO MENU (category_id, menu_name, price, description, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, menu.getCategoryId());
            pstmt.setString(2, menu.getMenuName());
            pstmt.setInt(3, menu.getPrice());
            pstmt.setString(4, menu.getDescription() == null ? "" : menu.getDescription());
            pstmt.setInt(5, menu.isAvailable() ? 1 : 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 등록 중 오류가 발생했습니다.", e);
        }
    }

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
            return menus;
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

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
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 조회 중 오류가 발생했습니다.", e);
        }
    }

    public boolean deleteMenu(long id) {
        String sql = "DELETE FROM MENU WHERE menu_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 삭제 중 오류가 발생했습니다.", e);
        }
    }
}
