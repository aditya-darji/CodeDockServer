import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conn {
    Connection c;
    Statement s;

    static final String DB_URL = "jdbc:mysql://localhost:3306/codedockdatabase";
    static final String USER = "root";
    static final String PASS = "aditya2000";

    public Conn() throws SQLException {
        System.out.println("Hello");
        c = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected to db");
        s = c.createStatement();
    }
}
