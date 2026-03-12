package repository;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    
    // 카테고리 추가
    public void addCategory(String name) {
        String sql = "INSERT INTO CATEGORY (category_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 카테고리 목록 조회 (Category 모델 리스트 반환으로 변경)
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // 카테고리 삭제
    public void deleteCategory(int id) {
        String sql = "DELETE FROM CATEGORY WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("카테고리 삭제 실패 (관련 상품이 있을 수 있습니다): " + e.getMessage());
        }
    }
}
