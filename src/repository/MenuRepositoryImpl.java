package repository;

import exception.RepositoryException;
import model.Menu;
import model.OptionGroup;
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
                Menu menu = new Menu(
                    rs.getLong("menu_id"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("menu_name"),
                    rs.getInt("price"),
                    rs.getString("description"),
                    rs.getBoolean("is_available"),
                    rs.getTimestamp("created_at")
                );
                menu.setOptionGroups(fetchOptionGroups(conn, menu.getMenuId(), menu.getCategoryId()));
                menus.add(menu);
            }
        } catch (SQLException e) {
            throw new RepositoryException("메뉴 목록 조회 중 오류가 발생했습니다.", e);
        }
        return menus;
    }

    public Menu findById(long id) {
        String sql = "SELECT m.*, c.category_name " +
                     "FROM MENU m " +
                     "LEFT JOIN CATEGORY c ON m.category_id = c.category_id " +
                     "WHERE m.menu_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Menu menu = new Menu(
                        rs.getLong("menu_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name") != null ? rs.getString("category_name") : "Unknown",
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("description"),
                        rs.getBoolean("is_available"),
                        rs.getTimestamp("created_at")
                    );
                    menu.setOptionGroups(fetchOptionGroups(conn, menu.getMenuId(), menu.getCategoryId()));
                    return menu;
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
                    Menu menu = new Menu(
                        rs.getLong("menu_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("description"),
                        rs.getBoolean("is_available"),
                        rs.getTimestamp("created_at")
                    );
                    menu.setOptionGroups(fetchOptionGroups(conn, menu.getMenuId(), menu.getCategoryId()));
                    menus.add(menu);
                }
            }
            return menus;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리별 메뉴 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    private List<OptionGroup> fetchOptionGroups(Connection conn, long menuId, int categoryId) {
        List<OptionGroup> groups = new ArrayList<>();
        
        // 1. 먼저 메뉴별 특화 옵션이 있는지 확인
        String menuSql = "SELECT og.group_id, og.group_name " +
                         "FROM MENU_OPTION_GROUP mog " +
                         "JOIN OPTION_GROUP og ON mog.group_id = og.group_id " +
                         "WHERE mog.menu_id = ? " +
                         "ORDER BY mog.display_order";
        
        try (PreparedStatement pstmt = conn.prepareStatement(menuSql)) {
            pstmt.setLong(1, menuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(new OptionGroup(rs.getLong("group_id"), rs.getString("group_name")));
                }
            }
        } catch (SQLException e) { }

        // 2. 메뉴별 옵션이 없으면 카테고리 기본 옵션을 가져옴
        if (groups.isEmpty()) {
            String categorySql = "SELECT og.group_id, og.group_name " +
                                 "FROM CATEGORY_OPTION_GROUP cog " +
                                 "JOIN OPTION_GROUP og ON cog.group_id = og.group_id " +
                                 "WHERE cog.category_id = ? " +
                                 "ORDER BY cog.display_order";
            try (PreparedStatement pstmt = conn.prepareStatement(categorySql)) {
                pstmt.setInt(1, categoryId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        groups.add(new OptionGroup(rs.getLong("group_id"), rs.getString("group_name")));
                    }
                }
            } catch (SQLException e) { }
        }
        
        return groups;
    }

    @Override
    public void addOptionGroupToMenu(long menuId, long groupId, int displayOrder) {
        String sql = "INSERT INTO MENU_OPTION_GROUP (menu_id, group_id, display_order) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuId);
            pstmt.setLong(2, groupId);
            pstmt.setInt(3, displayOrder);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("메뉴별 옵션 그룹 등록 중 오류가 발생했습니다.", e);
        }
    }
}

