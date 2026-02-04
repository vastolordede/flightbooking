package flightbooking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:postgresql://ep-spring-salad-a1ra5tb8-pooler.ap-southeast-1.aws.neon.tech:5432/neondb"
      + "?sslmode=require&channelBinding=require";

    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_NO8vqlZ4hoIL";   // đúng password bạn copy từ Neon

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL Driver loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to Neon...");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
