import java.sql.*;

public class TestQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://54.180.25.109:3306/kiosk?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user = "root";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                // 1. 기존 모든 카테고리-옵션 매핑 초기화
                stmt.executeUpdate("DELETE FROM CATEGORY_OPTION_GROUP");

                // 2. 커피 (ID: 1) 매핑: 사이즈(2), 온도(1), 샷 추가(6), 시럽 추가(7), 카페인유무(3)
                stmt.executeUpdate("INSERT INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (1, 2, 1), (1, 1, 2), (1, 6, 3), (1, 7, 4), (1, 3, 5)");

                // 3. 논커피 (ID: 2) 매핑: 사이즈(2), 온도(1), 퍼스널 옵션(8)
                stmt.executeUpdate("INSERT INTO CATEGORY_OPTION_GROUP (category_id, group_id, display_order) VALUES (2, 2, 1), (2, 1, 2), (2, 8, 3)");

                // 4. 디저트 (ID: 3)는 매핑하지 않음 (자동으로 비어있게 됨)

                // 5. 청포도에이드(및 다른 논커피) 개별 매핑 오류 방지
                // 개별 메뉴 매핑 테이블(MENU_OPTION_GROUP)이 있다면 거기서 불필요한 것 제거
                // (만약 개별 매핑을 사용 중이라면 아래 쿼리 실행)
                stmt.executeUpdate("DELETE mog FROM MENU_OPTION_GROUP mog " +
                                 "JOIN MENU m ON mog.menu_id = m.menu_id " +
                                 "WHERE m.menu_name LIKE '%청포도에이드%' OR m.category_id = 2 OR m.category_id = 3");

                conn.commit();
                System.out.println("Success: Category options reconfigured correctly!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}