package Service;

import java.sql.*;
import Database.*;
import Model.*;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
	public boolean registerUser(String firstName, String lastName, String email, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO user (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
        	statement.setString(1, firstName);
        	statement.setString(2, lastName);
        	statement.setString(3, email);
        	statement.setString(4, hashedPassword);
        	
        	statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                throw new RuntimeException("The email address is already registered. Please use a different email.");
            } else {
                System.err.println("Got exception while registering user: " + e.getLocalizedMessage());
                throw new RuntimeException("Registration failed due to a system error. Please try again later.");
            }
        }
    }
	
	public User loginUser(String email, String password) {
        String sql = "SELECT rowid, first_name, last_name, email, password FROM user WHERE email = ?";
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
        	statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
            	String storedHashedPassword = rs.getString("password");

                // verify the password
                if (verifyPassword(password, storedHashedPassword)) {
                	int id = rs.getInt("rowid");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    return new User(id, firstName, lastName, email, storedHashedPassword);
                }
            }
        } catch (SQLException e) {
        	System.err.println("Error during login: " + e.getLocalizedMessage());
        }
        return null;
    }
	
	public boolean verifyPassword(String password, String storedHash) {
		return BCrypt.checkpw(password, storedHash);
	}
	
	public String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public boolean validatePassword(int userId, String password) {
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement("SELECT password FROM user WHERE rowid = ?")) {
        	statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                return verifyPassword(password, storedHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public boolean updateUserPassword(int userId, String newPassword) {
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement("UPDATE user SET password = ? WHERE rowid = ?")) {
            String hashedPassword = hashPassword(newPassword);
            statement.setString(1, hashedPassword);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public boolean updateUserDetails(User user) {
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement("UPDATE user SET first_name = ?, last_name = ?, email = ? WHERE rowid = ?")) {
        	statement.setString(1, user.getFirstName());
        	statement.setString(2, user.getLastName());
        	statement.setString(3, user.getEmail());
        	statement.setInt(4, user.getId());
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
