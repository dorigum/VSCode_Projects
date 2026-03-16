package repository;

import model.OptionGroup;
import model.MenuOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionGroupRepositoryImpl implements OptionGroupRepository {

    @Override
    public List<OptionGroup> findAll() {
        String sql = "SELECT * FROM OPTION_GROUP";
        List<OptionGroup> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new OptionGroup(rs.getLong("group_id"), rs.getString("group_name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("옵션 그룹 조회 실패", e);
        }
        return list;
    }

    @Override
    public List<OptionGroup> findOptionGroupsWithOptionsByMenuId(long menuId) {
        // 메뉴별 전용 옵션 그룹만 조회
        String menuSql = """
            SELECT
                OG.group_id,
                OG.group_name,
                MO.option_id,
                MO.option_name,
                MO.extra_price,
                MO.display_order AS option_order
            FROM MENU_OPTION_GROUP MOG
            INNER JOIN OPTION_GROUP OG ON OG.group_id = MOG.group_id
            LEFT JOIN MENU_OPTION MO ON MO.group_id = OG.group_id
            WHERE MOG.menu_id = ?
            ORDER BY MOG.display_order, MO.display_order
            """;

        return fetchGroupsFromSql(menuSql, menuId);
    }

    private List<OptionGroup> fetchGroupsFromSql(String sql, long idParam) {
        Map<Long, OptionGroup> grouped = new LinkedHashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, idParam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long groupId = rs.getLong("group_id");
                    String groupName = rs.getString("group_name");

                    OptionGroup optionGroup = grouped.computeIfAbsent(groupId, id -> {
                        OptionGroup g = new OptionGroup(id, groupName);
                        g.setOptions(new ArrayList<>());
                        return g;
                    });

                    long optionId = rs.getLong("option_id");
                    if (optionId > 0) {
                        MenuOption option = new MenuOption(
                            optionId,
                            groupId,
                            rs.getString("option_name"),
                            rs.getInt("extra_price"),
                            rs.getInt("option_order")
                        );
                        optionGroup.addOption(option);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("옵션 그룹 조회 실패", e);
        }
        return new ArrayList<>(grouped.values());
    }


    @Override
    public OptionGroup findById(long groupId) {
        String sql = "SELECT * FROM OPTION_GROUP WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OptionGroup(rs.getLong("group_id"), rs.getString("group_name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("옵션 그룹 상세 조회 실패", e);
        }
        return null;
    }

    @Override
    public void save(OptionGroup optionGroup) {
        String sql = "INSERT INTO OPTION_GROUP (group_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, optionGroup.getGroupName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("옵션 그룹 저장 실패", e);
        }
    }

    @Override
    public void delete(long groupId) {
        String sql = "DELETE FROM OPTION_GROUP WHERE group_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("옵션 그룹 삭제 실패", e);
        }
    }

    
}
