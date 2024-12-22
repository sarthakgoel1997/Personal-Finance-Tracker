package Database;

import java.sql.*;

public class Database {
	private static final String URL = "jdbc:sqlite:financeTracker.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
        	System.err.println("Got exception while connecting to database: " + e.getLocalizedMessage());
        }
        return conn;
    }
}
