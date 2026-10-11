package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Encapsulation, Constructors, Object Relationships (Part of Order Composition)
 *
 * PURPOSE:
 * OrderItem represents a line item in a finalized customer order.
 * It permanently records the crop ID, crop name, quantity, and historical unit price
 * at the exact moment of order placement.
 */

public class OrderItem {

    // OOP CONCEPT: Encapsulation
    private int orderItemId;
    private int orderId;
    private int cropId;
    private String cropName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    // OOP CONCEPT: Constructor
    public OrderItem(int orderItemId, int orderId, int cropId, String cropName, int quantity, double unitPrice, double subtotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.cropId = cropId;
        this.cropName = cropName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // OOP CONCEPT: Constructor Overloading
    // Used before insertion into MySQL when orderItemId is auto-generated
    public OrderItem(int cropId, String cropName, int quantity, double unitPrice, double subtotal) {
        this(0, 0, cropId, cropName, quantity, unitPrice, subtotal);
    }

    // ARITHMETIC OPERATIONS:
    // Constructor deriving subtotal automatically from quantity and unit price
    public OrderItem(int cropId, String cropName, int quantity, double unitPrice) {
        this(0, 0, cropId, cropName, quantity, unitPrice, quantity * unitPrice);
    }

    // OOP CONCEPT: Getters and Setters
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCropId() {
        return cropId;
    }

    public void setCropId(int cropId) {
        this.cropId = cropId;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
