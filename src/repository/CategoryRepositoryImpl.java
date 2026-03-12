package repository;

import exception.RepositoryException;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "SELECT * FROM CATEGORY WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getInt("category_id"), rs.getString("category_name"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("카테고리 조회 중 오류가 발생했습니다.", e);
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORY ORDER BY category_id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                ));
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
}
