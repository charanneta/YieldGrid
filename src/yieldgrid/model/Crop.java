package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Encapsulation, Constructors, Business Validation, Object State
 *
 * PURPOSE:
 * Crop models an agricultural product offered by a farmer in YieldGrid.
 * It encapsulates crop identification, farmer ownership, stock availability,
 * and unit pricing.
 */

public class Crop {

    // OOP CONCEPT: Encapsulation
    // Private instance fields ensure data integrity and prevent external corruption
    private int cropId;
    private int farmerId;
    private String farmerName; // For display purposes in customer marketplace
    private String cropName;
    private String description;
    private int quantity;
    private double price;
    private boolean available;

    // OOP CONCEPT: Constructor
    // Full constructor initializing all crop attributes from database records
    public Crop(int cropId, int farmerId, String farmerName, String cropName, String description, int quantity, double price, boolean available) {
        this.cropId = cropId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.cropName = cropName;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.available = available;
    }

    // OOP CONCEPT: Constructor Overloading
    // Constructor without farmerName when only IDs and core fields are known
    public Crop(int cropId, int farmerId, String cropName, String description, int quantity, double price, boolean available) {
        this(cropId, farmerId, "", cropName, description, quantity, price, available);
    }

    // Constructor for creating a new Crop before database ID generation
    public Crop(int farmerId, String cropName, String description, int quantity, double price, boolean available) {
        this(0, farmerId, "", cropName, description, quantity, price, available);
    }

    // OOP CONCEPT: Methods - Encapsulation (Getters and Setters)
    public int getCropId() {
        return cropId;
    }

    public void setCropId(int cropId) {
        this.cropId = cropId;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // METHOD CONCEPT: Business Logic & Arithmetic Validation
    // Verifies whether a requested purchase quantity can be satisfied by current stock
    public boolean hasSufficientStock(int requestedQuantity) {
        return this.available && this.quantity >= requestedQuantity && requestedQuantity > 0;
    }

    // ARITHMETIC OPERATION:
    // Deducts stock quantity when order is placed
    public void deductStock(int amountToDeduct) {
        if (amountToDeduct > 0 && amountToDeduct <= this.quantity) {
            this.quantity -= amountToDeduct;
            if (this.quantity == 0) {
                this.available = false;
            }
        }
    }

    @Override
    public String toString() {
        return cropName + " (Rs. " + String.format("%.2f", price) + "/unit, Available: " + quantity + ")";
    }
}
