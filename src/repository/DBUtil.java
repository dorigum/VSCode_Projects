package repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static Properties props = new Properties();

    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("dbinfo.properties")) {
            if (is == null) {
                System.err.println("❌ [에러] dbinfo.properties 파일을 찾을 수 없습니다! (src/resources 폴더 확인)");
            } else {
                props.load(is);
                System.out.println("ℹ️ DB 설정 로드 완료: " + props.getProperty("db.url"));
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (Exception e) {
            System.err.println("❌ [치명적 에러] JDBC 드라이버 로드 또는 설정 파일 읽기 실패!");
            System.err.println("에러 내용: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
            );
        } catch (SQLException e) {
            System.err.println("❌ [DB 연결 실패] URL: " + props.getProperty("db.url"));
            System.err.println("에러 메시지: " + e.getMessage());
            throw e;
        }
    }
}
