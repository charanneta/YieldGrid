package yieldgrid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import yieldgrid.database.DBConnection;
import yieldgrid.model.Crop;

/*
 * COURSE CONCEPT:
 * JDBC, SQL CRUD (Create, Read, Update, Delete), PreparedStatement, ResultSet, Collections (ArrayList)
 *
 * PURPOSE:
 * CropDAO manages all persistent crop inventory operations against MySQL.
 * It provides full CRUD capabilities for farmers managing their produce,
 * as well as catalog query operations for customers.
 */

public class CropDAO {

    // CRUD CONCEPT: CREATE (INSERT)
    // Inserts a new crop record into the crops table
    public boolean addCrop(Crop crop) throws SQLException {
        String sql = "INSERT INTO crops (farmer_id, crop_name, description, quantity, price, available) VALUES (?, ?, ?, ?, ?, ?);";

        // JDBC & PreparedStatement:
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, crop.getFarmerId());
            pstmt.setString(2, crop.getCropName());
            pstmt.setString(3, crop.getDescription());
            pstmt.setInt(4, crop.getQuantity());
            pstmt.setDouble(5, crop.getPrice());
            pstmt.setBoolean(6, crop.isAvailable());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        crop.setCropId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // CRUD CONCEPT: READ (SELECT ALL FOR CUSTOMER BROWSE)
    // JAVA COLLECTIONS: ArrayList
    // Returns an ordered ArrayList of all active crops currently available in the marketplace
    public ArrayList<Crop> getAllAvailableCrops() throws SQLException {
        // SQL JOIN: Joins crops with users to retrieve the farmer's display name
        String sql = "SELECT c.crop_id, c.farmer_id, u.name AS farmer_name, c.crop_name, "
                   + "c.description, c.quantity, c.price, c.available "
                   + "FROM crops c "
                   + "JOIN users u ON c.farmer_id = u.user_id "
                   + "WHERE c.available = TRUE AND c.quantity > 0 "
                   + "ORDER BY c.crop_name ASC;";

        ArrayList<Crop> cropList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // DATABASE RETRIEVAL:
            // Loops through ResultSet and populates the ArrayList
            while (rs.next()) {
                Crop crop = new Crop(
                    rs.getInt("crop_id"),
                    rs.getInt("farmer_id"),
                    rs.getString("farmer_name"),
                    rs.getString("crop_name"),
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getBoolean("available")
                );
                cropList.add(crop);
            }
        }
        return cropList;
    }

    // CRUD CONCEPT: READ (SELECT CROPS BY FARMER)
    // Ensures a farmer only views their own listed crops (Access Control)
    public ArrayList<Crop> getCropsByFarmer(int farmerId) throws SQLException {
        String sql = "SELECT crop_id, farmer_id, crop_name, description, quantity, price, available "
                   + "FROM crops WHERE farmer_id = ? ORDER BY crop_id DESC;";

        ArrayList<Crop> cropList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Crop crop = new Crop(
                        rs.getInt("crop_id"),
                        rs.getInt("farmer_id"),
                        "",
                        rs.getString("crop_name"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                    );
                    cropList.add(crop);
                }
            }
        }
        return cropList;
    }

    // CRUD CONCEPT: READ (SELECT SINGLE CROP)
    public Crop getCropById(int cropId) throws SQLException {
        String sql = "SELECT c.crop_id, c.farmer_id, u.name AS farmer_name, c.crop_name, "
                   + "c.description, c.quantity, c.price, c.available "
                   + "FROM crops c "
                   + "JOIN users u ON c.farmer_id = u.user_id "
                   + "WHERE c.crop_id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cropId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Crop(
                        rs.getInt("crop_id"),
                        rs.getInt("farmer_id"),
                        rs.getString("farmer_name"),
                        rs.getString("crop_name"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                    );
                }
            }
        }
        return null;
    }

    // CRUD CONCEPT: UPDATE
    // Updates details of an existing crop; checks farmer_id to prevent tampering
    public boolean updateCrop(Crop crop) throws SQLException {
        String sql = "UPDATE crops SET crop_name = ?, description = ?, quantity = ?, price = ?, available = ? "
                   + "WHERE crop_id = ? AND farmer_id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, crop.getCropName());
            pstmt.setString(2, crop.getDescription());
            pstmt.setInt(3, crop.getQuantity());
            pstmt.setDouble(4, crop.getPrice());
            // Automatically set available to false if quantity reaches 0
            boolean avail = crop.isAvailable() && crop.getQuantity() > 0;
            pstmt.setBoolean(5, avail);
            pstmt.setInt(6, crop.getCropId());
            pstmt.setInt(7, crop.getFarmerId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // CRUD CONCEPT: DELETE
    // Removes a crop record from MySQL; enforces farmer authorization
    public boolean deleteCrop(int cropId, int farmerId) throws SQLException {
        String sql = "DELETE FROM crops "
                   + "WHERE crop_id = ? AND farmer_id = ? "
                   + "AND NOT EXISTS (SELECT 1 FROM order_items WHERE order_items.crop_id = crops.crop_id);";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cropId);
            pstmt.setInt(2, farmerId);

            return pstmt.executeUpdate() > 0;
        }
    }
}
