import java.sql.*;

public class TestQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/kiosk?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user = "root";
        String password = "password"; // Assuming root/password or we can read from dbinfo.properties

        try {
            java.util.Properties props = new java.util.Properties();
            props.load(new java.io.FileInputStream("C:\\VSCode_Projects\\1_Cafe_kiosk\\resources\\dbinfo.properties"));
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            
            Connection conn = DriverManager.getConnection(url, user, password);
            String sql = "SELECT m.*, c.category_name, og.group_id, og.group_name " +
                     "FROM MENU m " +
                     "JOIN CATEGORY c ON m.category_id = c.category_id " +
                     "LEFT JOIN CATEGORY_OPTION_GROUP cog ON c.category_id = cog.category_id " +
                     "LEFT JOIN OPTION_GROUP og ON cog.group_id = og.group_id " +
                     "ORDER BY m.menu_id, cog.display_order";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("menu_id: " + rs.getLong("menu_id"));
                System.out.println("is_available: " + rs.getBoolean("is_available"));
            }
            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
