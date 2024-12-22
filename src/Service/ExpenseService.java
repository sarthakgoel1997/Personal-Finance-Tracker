package Service;

import java.sql.*;
import java.util.*;
import Database.*;
import Model.*;

public class ExpenseService {
	public boolean addExpense(int userId, double amount, String vendor, String category) {
	    String sql = "INSERT INTO expense (user_id, amount, vendor, category) VALUES (?, ?, ?, ?)";
	    try (Connection conn = Database.connect();
	         PreparedStatement statement = conn.prepareStatement(sql)) {
	        statement.setInt(1, userId);
	        statement.setDouble(2, amount);
	        statement.setString(3, vendor);
	        statement.setString(4, category);

	        statement.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        System.err.println("Error while adding expense: " + e.getLocalizedMessage());
	        return false;
	    }
	}
	
	public Map<String, Double> getExpensesByCategory(int userId, String timeRange) {
		String sqlQuery = "SELECT category, SUM(amount) AS total_amount FROM expense WHERE user_id = ? AND created_at >= ? GROUP BY category";
        Map<String, Double> expenseData = new HashMap<>();

        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            statement.setInt(1, userId);

            java.sql.Timestamp startTimestamp = getStartTimestamp(timeRange);
            statement.setString(2, startTimestamp.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String category = resultSet.getString("category");
                    double totalAmount = resultSet.getDouble("total_amount");
                    expenseData.put(category, totalAmount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expenses by category: " + e.getLocalizedMessage());
        }

        return expenseData;
    }
	
	private java.sql.Timestamp getStartTimestamp(String timeRange) {
		// calculate date ranges for the selected time range
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        java.sql.Timestamp startTimestamp = null;
        
        switch (timeRange) {
        	case "Week":
        		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                break;
            
        	case "Month":
        		calendar.set(Calendar.DAY_OF_MONTH, 1);
        		break;
        	
        	case "Year":
        		calendar.set(Calendar.DAY_OF_YEAR, 1);
        	
        }
        
        startTimestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        
        // convert to GMT zone
        startTimestamp = new java.sql.Timestamp(startTimestamp.getTime() - calendar.getTimeZone().getOffset(startTimestamp.getTime()));
        return startTimestamp;
	}
	
	public Double getTotalExpenseByTimeRange(int userId, String timeRange) {
		String sqlQuery = "SELECT SUM(amount) AS total_amount FROM expense WHERE user_id = ? AND created_at >= ?";
	    Double totalExpense = 0.0;

	    try (Connection conn = Database.connect();
	         PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
	    	statement.setInt(1, userId);
	        java.sql.Timestamp startTimestamp = getStartTimestamp(timeRange);
            statement.setString(2, startTimestamp.toString());

	        try (ResultSet rs = statement.executeQuery()) {
	            while (rs.next()) {
	            	totalExpense = rs.getDouble("total_amount");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return totalExpense;
	}
	
	public List<Expense> getExpenses(int userId) {
	    List<Expense> expenses = new ArrayList<>();

	    String query = "SELECT rowid, user_id, amount, vendor, category, created_at FROM expense WHERE user_id = ? ORDER BY created_at DESC";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, userId);
	        ResultSet resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            Expense expense = new Expense(
	                resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("vendor"),
	                resultSet.getString("category"),
	                resultSet.getString("created_at")
	            );
	            expenses.add(expense);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return expenses;
	}
	
	public Expense getExpenseById(int expenseId) {
	    String query = "SELECT rowid, user_id, amount, vendor, category, created_at FROM expense WHERE rowid = ?";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, expenseId);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            Expense expense = new Expense(
            		resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("vendor"),
	                resultSet.getString("category"),
	                resultSet.getString("created_at")
	            );
	            return expense;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean updateExpense(Expense expense) {
	    String query = "UPDATE expense SET category = ?, amount = ?, vendor = ? WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setString(1, expense.getCategory());
	        statement.setDouble(2, expense.getAmount());
	        statement.setString(3, expense.getVendor());
	        statement.setInt(4, expense.getId());

	        int rowsAffected = statement.executeUpdate();

	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public void deleteExpense(int expenseId) {
	    String query = "DELETE FROM expense WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, expenseId);
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
