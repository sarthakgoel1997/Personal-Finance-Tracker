package Service;

import java.sql.*;
import java.util.*;
import Database.*;
import Model.*;

public class BudgetService {
	public boolean addBudget(int userId, String category, double amount, String frequency) {
        String sql = "INSERT INTO budget (user_id, category, amount, frequency) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, category);
            statement.setDouble(3, amount);
            statement.setString(4, frequency);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error while adding budget: " + e.getMessage());
            return false;
        }
    }
	
	public Map<String, Map<String, Map<String, Double>>> getBudgetsAndExpenses(int userId) {
        String sqlQuery = "SELECT category, amount, frequency FROM budget WHERE user_id = ? ORDER BY frequency, category";
        Map<String, Map<String, Map<String, Double>>> result = new HashMap<>();
        ExpenseService expenseService = new ExpenseService();

        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String category = resultSet.getString("category");
                    double budgetAmount = resultSet.getDouble("amount");
                    String frequency = resultSet.getString("frequency");
                    
                    // fetch actual expense for the category and time range
                    Map<String, Double> expenseData = expenseService.getExpensesByCategory(userId, frequency);
                    double actualExpense = expenseData.getOrDefault(category, 0.0);

                    // group by frequency, then category
                    result.putIfAbsent(frequency, new HashMap<>());
                    Map<String, Map<String, Double>> frequencyMap = result.get(frequency);
                    frequencyMap.put(category, Map.of("Budget", budgetAmount, "Expense", actualExpense));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budgets: " + e.getLocalizedMessage());
        }
        
        return result;
    }
	
	public List<Budget> getBudgets(int userId) {
	    List<Budget> budgets = new ArrayList<>();

	    String query = "SELECT rowid, user_id, category, frequency, amount, created_at FROM budget WHERE user_id = ? ORDER BY created_at DESC";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, userId);
	        ResultSet resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            Budget budget = new Budget(
	                resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getString("category"),
	                resultSet.getString("frequency"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("created_at")
	            );
	            budgets.add(budget);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return budgets;
	}
	
	public Budget getBudgetById(int budgetId) {
	    String query = "SELECT rowid, user_id, category, frequency, amount, created_at FROM budget WHERE rowid = ?";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, budgetId);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            Budget budget = new Budget(
            		resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getString("category"),
	                resultSet.getString("frequency"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("created_at")
	            );
	            return budget;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean updateBudget(Budget budget) {
	    String query = "UPDATE budget SET category = ?, frequency = ?, amount = ? WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setString(1, budget.getCategory());
	        statement.setString(2, budget.getFrequency());
	        statement.setDouble(3, budget.getAmount());
	        statement.setInt(4, budget.getId());

	        int rowsAffected = statement.executeUpdate();

	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public void deleteBudget(int budgetId) {
	    String query = "DELETE FROM budget WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, budgetId);
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
