import java.sql.*;

public class TestQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://54.180.25.109:3306/kiosk?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user = "root";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                // 1. 커피 카테고리 ID 찾기
                int coffeeCatId = -1;
                try (ResultSet rs = stmt.executeQuery("SELECT category_id FROM CATEGORY WHERE category_name LIKE '%커피%' OR category_name LIKE '%Coffee%'")) {
                    if (rs.next()) coffeeCatId = rs.getInt(1);
                }

                // 2. 옵션 그룹 ID들 찾기
                int shotId = -1, syrupId = -1, personalId = -1;
                try (ResultSet rs = stmt.executeQuery("SELECT group_id, group_name FROM OPTION_GROUP WHERE group_name IN ('샷 추가', '시럽 추가', '퍼스널 옵션')")) {
                    while (rs.next()) {
                        String name = rs.getString(2);
                        if (name.equals("샷 추가")) shotId = rs.getInt(1);
                        else if (name.equals("시럽 추가")) syrupId = rs.getInt(1);
                        else if (name.equals("퍼스널 옵션")) personalId = rs.getInt(1);
                    }
                }

                // 3. 커피 카테고리에 매핑 (중복 무시)
                if (coffeeCatId != -1) {
                    if (shotId != -1) stmt.executeUpdate("INSERT IGNORE INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (" + coffeeCatId + ", " + shotId + ", 3)");
                    if (syrupId != -1) stmt.executeUpdate("INSERT IGNORE INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (" + coffeeCatId + ", " + syrupId + ", 4)");
                    if (personalId != -1) stmt.executeUpdate("INSERT IGNORE INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (" + coffeeCatId + ", " + personalId + ", 5)");
                    System.out.println("Success: Mapped shot/syrup options to Coffee category!");
                } else {
                    System.out.println("Warning: Coffee category not found.");
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}