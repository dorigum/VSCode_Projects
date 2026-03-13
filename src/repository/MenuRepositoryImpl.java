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
        String sql = "SELECT m.*, c.category_name " +
                     "FROM MENU m " +
                     "JOIN CATEGORY c ON m.category_id = c.category_id " +
                     "ORDER BY m.menu_id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                menus.add(new Menu(
                    rs.getLong("menu_id"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("menu_name"),
                    rs.getInt("price"),
                    rs.getString("description"),
                    rs.getBoolean("is_available"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 목록 조회 중 오류가 발생했습니다.", e);
        }
        return menus;
    }

    public Menu findById(long id) {
        String sql = "SELECT * FROM MENU WHERE menu_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Note: findById doesn't currently join with category, 
                    // if categoryName is needed here too, we should update the SQL.
                    // For now, keeping it consistent with the model's new constructor.
                    return new Menu(
                        rs.getLong("menu_id"),
                        rs.getInt("category_id"),
                        "Unknown", // Placeholder or perform join
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

    public List<Menu> getMenusByCategoryName(String categoryName) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.*, c.category_name " +
                     "FROM MENU m " +
                     "JOIN CATEGORY c ON m.category_id = c.category_id " +
                     "WHERE c.category_name = ? " +
                     "ORDER BY m.menu_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(new Menu(
                        rs.getLong("menu_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("description"),
                        rs.getBoolean("is_available"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
            return menus;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리별 메뉴 목록 조회 중 오류가 발생했습니다.", e);
        }
    }
}
