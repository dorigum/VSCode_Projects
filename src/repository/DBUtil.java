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
                System.err.println("dbinfo.properties 파일을 찾을 수 없습니다!");
            } else {
                props.load(is);
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (Exception e) {
            System.err.println("JDBC 드라이버 로드 또는 프로퍼티 파일 읽기 실패!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}
