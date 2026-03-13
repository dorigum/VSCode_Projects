package repository;

import model.MenuOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuOptionRepositoryImpl implements MenuOptionRepository {

    @Override
    public List<MenuOption> findAll() {
        String sql = "SELECT * FROM MENU_OPTION ORDER BY group_id, display_order";
        List<MenuOption> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("메뉴 옵션 조회 실패", e);
        }
        return list;
    }

    @Override
    public MenuOption findById(long optionId) {
        String sql = "SELECT * FROM MENU_OPTION WHERE option_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, optionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("메뉴 옵션 상세 조회 실패", e);
        }
        return null;
    }

    @Override
    public List<MenuOption> findByGroupId(long groupId) {
        String sql = "SELECT * FROM MENU_OPTION WHERE group_id = ? ORDER BY display_order";
        List<MenuOption> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("그룹별 옵션 조회 실패", e);
        }
        return list;
    }

    @Override
    public void save(MenuOption menuOption) {
        String sql = "INSERT INTO MENU_OPTION (group_id, option_name, extra_price, display_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuOption.getGroupId());
            pstmt.setString(2, menuOption.getOptionName());
            pstmt.setInt(3, menuOption.getExtraPrice());
            pstmt.setInt(4, menuOption.getDisplayOrder());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("메뉴 옵션 저장 실패", e);
        }
    }

    @Override
    public void update(MenuOption menuOption) {
        String sql = "UPDATE MENU_OPTION SET option_name = ?, extra_price = ?, display_order = ? WHERE option_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menuOption.getOptionName());
            pstmt.setInt(2, menuOption.getExtraPrice());
            pstmt.setInt(3, menuOption.getDisplayOrder());
            pstmt.setLong(4, menuOption.getOptionId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("메뉴 옵션 수정 실패", e);
        }
    }

    @Override
    public void delete(long optionId) {
        String sql = "DELETE FROM MENU_OPTION WHERE option_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, optionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("메뉴 옵션 삭제 실패", e);
        }
    }

    private MenuOption extractFromResultSet(ResultSet rs) throws SQLException {
        return new MenuOption(
            rs.getLong("option_id"),
            rs.getLong("group_id"),
            rs.getString("option_name"),
            rs.getInt("extra_price"),
            rs.getInt("display_order")
        );
    }
}
