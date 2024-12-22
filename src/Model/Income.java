package Model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Income {
	private int id;
	private int userId;
	private String source;
	private String frequency;
	private double amount;
    private String createdAt;
    
	public Income(int id, int userId, String source, String frequency, double amount, String createdAt) {
		this.id = id;
		this.userId = userId;
		this.source = source;
		this.frequency = frequency;
		this.amount = amount;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public String getSource() {
		return source;
	}
	
	public String getFrequency() {
		return frequency;
	}

	public double getAmount() {
		return amount;
	}

	public String getCreatedAt() {
		return createdAt;
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
