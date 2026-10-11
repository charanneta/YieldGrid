package yieldgrid.service;

import java.sql.SQLException;
import java.util.ArrayList;
import yieldgrid.dao.CropDAO;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.Crop;

/*
 * COURSE CONCEPT:
 * Business Validation, CRUD Logic, Authorization Checks, Collections (ArrayList), Exception Handling
 *
 * PURPOSE:
 * CropService provides a layer of business logic and validation for inventory management.
 * It enforces positive pricing and stock quantities and ensures farmers can only alter
 * crops they personally own.
 */

public class CropService {

    private final CropDAO cropDAO;

    public CropService() {
        this.cropDAO = new CropDAO();
    }

    public CropService(CropDAO cropDAO) {
        this.cropDAO = cropDAO;
    }

    // CRUD: CREATE & VALIDATION
    public Crop addCrop(int farmerId, String cropName, String description, int quantity, double price, boolean available)
            throws ValidationException, SQLException {

        // STRING HANDLING:
        if (cropName == null || cropName.trim().isEmpty()) {
            throw new ValidationException("Crop name cannot be empty.");
        }
        // ARITHMETIC / CONDITIONAL LOGIC:
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }
        if (price <= 0.0) {
            throw new ValidationException("Unit price must be greater than zero.");
        }

        Crop crop = new Crop(farmerId, cropName.trim(), description != null ? description.trim() : "", quantity, price, available);
        boolean success = cropDAO.addCrop(crop);
        if (!success) {
            throw new SQLException("Failed to save crop into inventory database.");
        }
        return crop;
    }

    // CRUD: READ
    // JAVA COLLECTIONS: ArrayList
    public ArrayList<Crop> getCropsByFarmer(int farmerId) throws SQLException {
        return cropDAO.getCropsByFarmer(farmerId);
    }

    // CRUD: READ
    public ArrayList<Crop> getAllAvailableCrops() throws SQLException {
        return cropDAO.getAllAvailableCrops();
    }

    // CRUD: UPDATE & AUTHORIZATION
    public boolean updateCrop(Crop crop, int requestingFarmerId) throws ValidationException, SQLException {
        if (crop == null) {
            throw new ValidationException("Crop cannot be null.");
        }
        // ACCESS CONTROL: Enforces that farmer can only update their own crop
        if (crop.getFarmerId() != requestingFarmerId) {
            throw new ValidationException("Access Denied: You do not have permission to modify another farmer's crop.");
        }
        if (crop.getCropName() == null || crop.getCropName().trim().isEmpty()) {
            throw new ValidationException("Crop name cannot be empty.");
        }
        if (crop.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }
        if (crop.getPrice() <= 0.0) {
            throw new ValidationException("Price must be strictly positive.");
        }

        return cropDAO.updateCrop(crop);
    }

    // CRUD: DELETE & AUTHORIZATION
    public boolean deleteCrop(int cropId, int requestingFarmerId) throws ValidationException, SQLException {
        Crop existing = cropDAO.getCropById(cropId);
        if (existing == null) {
            throw new ValidationException("Crop record not found.");
        }
        // ACCESS CONTROL:
        if (existing.getFarmerId() != requestingFarmerId) {
            throw new ValidationException("Access Denied: You cannot delete crops listed by other farmers.");
        }

        return cropDAO.deleteCrop(cropId, requestingFarmerId);
    }
}
