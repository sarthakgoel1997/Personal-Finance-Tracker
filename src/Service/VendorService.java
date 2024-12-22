package Service;

import java.sql.*;
import java.util.*;
import Database.*;

public class VendorService {
    public Map<String, String> getVendorCategoryMappings() {
        String sql = "SELECT vendor, category FROM vendor_category ORDER BY vendor ASC";
        Map<String, String> vendorCategoryMap = new LinkedHashMap<>();

        try (Connection conn = Database.connect();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String vendor = resultSet.getString("vendor");
                String category = resultSet.getString("category");
                vendorCategoryMap.put(vendor, category);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching vendor-category mappings: " + e.getLocalizedMessage());
        }

        return vendorCategoryMap;
    }
}
