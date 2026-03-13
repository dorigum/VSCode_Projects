import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

public class ListTables {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("C:\\VSCode_Projects\\1_Cafe_kiosk\\resources\\dbinfo.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            Connection conn = DriverManager.getConnection(url, user, password);
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            System.out.println("Tables in database:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
