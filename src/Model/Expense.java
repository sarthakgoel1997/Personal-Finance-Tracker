package Model;

import java.time.*;
import java.time.format.*;

public class Expense {
	private int id;
	private int userId;
	private double amount;
	private String vendor;
    private String category;
    private String createdAt;
    
	public Expense(int id, int userId, double amount, String vendor, String category, String created_at) {
		this.id = id;
		this.userId = userId;
		this.amount = amount;
		this.vendor = vendor;
		this.category = category;
		this.createdAt = created_at;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public double getAmount() {
		return amount;
	}

	public String getVendor() {
		return vendor;
	}

	public String getCategory() {
		return category;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCreatedAtLocalTime() {
		try {
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        LocalDateTime gmtDateTime = LocalDateTime.parse(createdAt, inputFormatter);
	        ZonedDateTime gmtZonedDateTime = gmtDateTime.atZone(ZoneId.of("GMT"));
	        ZonedDateTime localZonedDateTime = gmtZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d yyyy - h:mm a");
	        return localZonedDateTime.format(outputFormatter);
	    } catch (DateTimeParseException e) {
	        e.printStackTrace();
	        return createdAt;
	    }
	}
}
