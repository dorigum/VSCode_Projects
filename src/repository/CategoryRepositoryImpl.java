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
        Category category = null;
        String catSql = "SELECT category_id, category_name FROM CATEGORY WHERE category_id = ?";
        String optSql = "SELECT group_id FROM CATEGORY_OPTION_GROUP WHERE category_id = ? ORDER BY display_order";
                     
        try (Connection conn = DBUtil.getConnection()) {
            // 1. 카테고리 기본 정보 조회
            try (PreparedStatement pstmt = conn.prepareStatement(catSql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        category = new Category(rs.getInt("category_id"), rs.getString("category_name"));
                    }
                }
            }

            // 2. 카테고리가 존재하면 매핑된 옵션 그룹 ID들 조회
            if (category != null) {
                List<Integer> groupIds = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(optSql)) {
                    pstmt.setInt(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            groupIds.add(rs.getInt("group_id"));
                        }
                    }
                }

                // 3. 각 그룹 ID에 해당하는 실제 OptionGroup 정보 채우기 (기존 레포지토리 활용 가능 시점)
                OptionGroupRepository ogRepo = new OptionGroupRepositoryImpl();
                for (int gid : groupIds) {
                    OptionGroup og = ogRepo.findById(gid);
                    if (og != null) {
                        category.addOptionGroup(og);
                    }
                }
            }
            return category;
        } catch (SQLException e) {
            // 구체적인 에러 메시지를 포함하여 원인 파악 용이하게 변경
            throw new RepositoryException("카테고리 상세 조회 중 DB 오류 발생: " + e.getMessage(), e);
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM CATEGORY ORDER BY category_id";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
            }
            return categories;
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
