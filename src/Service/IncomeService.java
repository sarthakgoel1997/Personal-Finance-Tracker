package Service;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import Database.Database;
import Model.Income;

public class IncomeService {
	public boolean addIncome(int userId, String source, String frequency, double amount) {
        String sql = "INSERT INTO income (user_id, source, frequency, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, source);
            statement.setString(3, frequency);
            statement.setDouble(4, amount);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error while adding income: " + e.getMessage());
            return false;
        }
    }
	
	public double getTotalIncomeByTimeRange(int userId, String timeRange) {
	    double totalIncome = 0.0;

	    String weeklyQuery = "SELECT amount, created_at FROM income WHERE user_id = ? AND frequency = 'Week'";
	    String monthlyQuery = "SELECT amount, created_at FROM income WHERE user_id = ? AND frequency = 'Month'";

	    try (Connection conn = Database.connect()) {
	        PreparedStatement statement;
	        ResultSet rs;

	        // current date to filter by created_at
	        String currentDate = getCurrentDateTime();
	        
	        switch (timeRange) {
	        	case "Week":
	        		statement = conn.prepareStatement(weeklyQuery);
		        	statement.setInt(1, userId);
		            rs = statement.executeQuery();
		            while (rs.next()) {
		                double amount = rs.getDouble("amount");
		                totalIncome += amount;
		            }
		            break;
		            
	        	case "Month":
	        		statement = conn.prepareStatement(weeklyQuery);
		        	statement.setInt(1, userId);
		            rs = statement.executeQuery();
		            while (rs.next()) {
		                double amount = rs.getDouble("amount");
		                String createdAt = rs.getString("created_at");
		                int weeksElapsed = getElapsedWeeks(createdAt, currentDate);
		                totalIncome += amount * weeksElapsed;
		            }
		            
		            statement = conn.prepareStatement(monthlyQuery);
		        	statement.setInt(1, userId);
		            rs = statement.executeQuery();
		            while (rs.next()) {
		                double amount = rs.getDouble("amount");
		                totalIncome += amount;
		            }
		            break;
		           
	        	case "Year":
	        		statement = conn.prepareStatement(weeklyQuery);
		        	statement.setInt(1, userId);
		            rs = statement.executeQuery();
		            while (rs.next()) {
		                double amount = rs.getDouble("amount");
		                String createdAt = rs.getString("created_at");
		                int weeksElapsed = getElapsedWeeks(createdAt, currentDate);
		                totalIncome += amount * weeksElapsed;
		            }
		            
		            statement = conn.prepareStatement(monthlyQuery);
		        	statement.setInt(1, userId);
		            rs = statement.executeQuery();
		            while (rs.next()) {
		                double amount = rs.getDouble("amount");
		                String createdAt = rs.getString("created_at");
		                int monthsElapsed = getElapsedMonths(createdAt, currentDate);
		                totalIncome += amount * monthsElapsed;
		            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return totalIncome;
	}
	
	private int getElapsedWeeks(String startDate, String endDate) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date start;
		try {
			start = formatter.parse(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	    Date end;
		try {
			end = formatter.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

	    long diffInMillis = end.getTime() - start.getTime();
	    return (int) (diffInMillis / (7 * 24 * 60 * 60 * 1000) + 1); // convert milliseconds to weeks
	}
	
	private int getElapsedMonths(String startDate, String endDate) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Calendar start = Calendar.getInstance();
	    Calendar end = Calendar.getInstance();

	    try {
			start.setTime(formatter.parse(startDate));
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	    try {
			end.setTime(formatter.parse(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

	    int yearsDifference = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
	    int monthsDifference = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);

	    return (yearsDifference * 12) + monthsDifference + 1; // total months elapsed
	}
	

	public String getCurrentDateTime() {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date currentDate = new Date();
	    return formatter.format(currentDate);
	}
	
	public List<Income> getIncomes(int userId) {
	    List<Income> incomes = new ArrayList<>();

	    String query = "SELECT rowid, user_id, source, frequency, amount, created_at FROM income WHERE user_id = ? ORDER BY created_at DESC";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, userId);
	        ResultSet resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            Income income = new Income(
	                resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getString("source"),
	                resultSet.getString("frequency"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("created_at")
	            );
	            incomes.add(income);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return incomes;
	}
	
	public Income getIncomeById(int incomeId) {
	    String query = "SELECT rowid, user_id, source, frequency, amount, created_at FROM income WHERE rowid = ?";
	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, incomeId);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            Income income = new Income(
            		resultSet.getInt("rowid"),
	                resultSet.getInt("user_id"),
	                resultSet.getString("source"),
	                resultSet.getString("frequency"),
	                resultSet.getDouble("amount"),
	                resultSet.getString("created_at")
	            );
	            return income;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean updateIncome(Income income) {
	    String query = "UPDATE income SET source = ?, frequency = ?, amount = ? WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setString(1, income.getSource());
	        statement.setString(2, income.getFrequency());
	        statement.setDouble(3, income.getAmount());
	        statement.setInt(4, income.getId());

	        int rowsAffected = statement.executeUpdate();

	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public void deleteIncome(int incomeId) {
	    String query = "DELETE FROM income WHERE rowid = ?";

	    try (Connection connection = Database.connect();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, incomeId);
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
