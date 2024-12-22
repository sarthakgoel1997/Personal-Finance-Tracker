package Model;

public class User {
	private int id;
	private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    
	public User(int id, String firstName, String lastName, String email, String passwordHash) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public int getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
