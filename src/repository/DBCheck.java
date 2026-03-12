package repository;

import java.sql.*;

public class DBCheck {
    public static void main(String[] args) {
        System.out.println("===== [DB 접속 테스트 및 데이터 확인] =====");
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("✅ 접속 성공: " + conn.getMetaData().getURL());
            
            // 1. 테이블 목록 조회
            System.out.println("\n[테이블 목록]");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables("kiosk", null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.print("- " + tableName);
                
                // 2. 각 테이블의 데이터 건수 조회
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`")) {
                    if (rs.next()) {
                        System.out.println(" (" + rs.getInt(1) + "건)");
                    }
                } catch (SQLException e) {
                    System.out.println(" (조회 실패)");
                }
            }
            
            // 3. 주요 데이터 샘플 조회 (MEMBER, MENU)
            System.out.println("\n[최근 가입 회원 샘플 (MEMBER)]");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT phone, role FROM MEMBER LIMIT 3")) {
                while (rs.next()) {
                    System.out.println("  * " + rs.getString("phone") + " (" + rs.getString("role") + ")");
                }
            } catch (SQLException e) {}

            System.out.println("\n[등록된 메뉴 샘플 (MENU)]");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT menu_name, price FROM MENU LIMIT 3")) {
                while (rs.next()) {
                    System.out.println("  * " + rs.getString("menu_name") + " (" + rs.getInt("price") + "원)");
                }
            } catch (SQLException e) {}

        } catch (SQLException e) {
            System.err.println("❌ DB 접속 실패!");
            e.printStackTrace();
        }
    }
}
