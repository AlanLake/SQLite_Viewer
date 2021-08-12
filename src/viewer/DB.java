package viewer;

import java.sql.*;
import java.util.ArrayList;

public class DB {

    public static String url;
    public static void connection(String fileName){
        url = "jdbc:sqlite:C:" + fileName;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
    public static ArrayList<String> getTableNames() {
        String sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%'";
        ArrayList<String> tables = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tables.add(rs.getString("name"));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tables;
    }
}
