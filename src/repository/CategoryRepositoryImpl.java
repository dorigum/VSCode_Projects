package repository;

import exception.RepositoryException;
import model.Category;
import model.OptionGroup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class CategoryRepositoryImpl implements CategoryRepository {

    public boolean addCategory(String name) {
        String sql = "INSERT INTO CATEGORY (category_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리 추가 중 오류가 발생했습니다.", e);
        }
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT c.category_id, c.category_name, og.group_id, og.group_name " +
                     "FROM CATEGORY c " +
                     "LEFT JOIN CATEGORY_OPTION_GROUP cog ON c.category_id = cog.category_id " +
                     "LEFT JOIN OPTION_GROUP og ON cog.group_id = og.group_id " +
                     "WHERE c.category_id = ? " +
                     "ORDER BY cog.display_order";
                     
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                Category category = null;
                while (rs.next()) {
                    if (category == null) {
                        category = new Category(rs.getInt("category_id"), rs.getString("category_name"));
                    }
                    long groupId = rs.getLong("group_id");
                    if (groupId > 0) { // 옵션 그룹이 매핑되어 있는 경우
                        category.addOptionGroup(new OptionGroup(groupId, rs.getString("group_name")));
                    }
                }
                return category;
            }
        } catch (SQLException e) {
            throw new RepositoryException("카테고리 상세 조회 중 오류가 발생했습니다.", e);
        }
    }

    public List<Category> getAllCategories() {
        Map<Integer, Category> categoryMap = new LinkedHashMap<>();
        String sql = "SELECT c.category_id, c.category_name, og.group_id, og.group_name " +
                     "FROM CATEGORY c " +
                     "LEFT JOIN CATEGORY_OPTION_GROUP cog ON c.category_id = cog.category_id " +
                     "LEFT JOIN OPTION_GROUP og ON cog.group_id = og.group_id " +
                     "ORDER BY c.category_id, cog.display_order";
                     
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("category_id");
                Category category = categoryMap.computeIfAbsent(id, k -> {
                    try {
                        return new Category(id, rs.getString("category_name"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                
                long groupId = rs.getLong("group_id");
                if (groupId > 0) {
                    category.addOptionGroup(new OptionGroup(groupId, rs.getString("group_name")));
                }
            }
            return new ArrayList<>(categoryMap.values());
        } catch (SQLException e) {
            throw new RepositoryException("카테고리 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM CATEGORY WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public boolean addOptionGroupToCategory(int categoryId, long groupId, int displayOrder) {
        String sql = "INSERT INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setLong(2, groupId);
            pstmt.setInt(3, displayOrder);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리별 옵션 그룹 등록에 실패했습니다.", e);
        }
    }

    @Override
    public boolean removeOptionGroupFromCategory(int categoryId, long groupId) {
        String sql = "DELETE FROM CATEGORY_OPTION_GROUP WHERE category_id = ? AND group_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setLong(2, groupId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리별 옵션 그룹 삭제에 실패했습니다.", e);
        }
    }
}
