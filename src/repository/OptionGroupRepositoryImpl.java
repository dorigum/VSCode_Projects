package repository;

import model.OptionGroup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
