package Model;

public class VendorCategory {
	private int id;
	private String vendor;
    private String category;
    private String createdAt;
    
    public VendorCategory(int id, String vendor, String category, String createdAt) {
		this.id = id;
		this.vendor = vendor;
		this.category = category;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
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
}
