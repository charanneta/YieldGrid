package yieldgrid.model;

import java.util.ArrayList;
import java.util.Date;

/*
 * COURSE CONCEPT:
 * Object Composition (HAS-A relationship), Collections (ArrayList), Encapsulation, Arithmetic Operations
 *
 * PURPOSE:
 * Order models a purchase order in YieldGrid. It links a Customer to a Farmer
 * and contains an ordered collection of OrderItem elements.
 */

public class Order {

    // OOP CONCEPT: Encapsulation
    private int orderId;
    private int customerId;
    private String customerName; // Display field
    private int farmerId;
    private String farmerName;   // Display field
    private Date orderDate;
    private double totalAmount;
    private OrderStatus status;

    // OOP CONCEPT: Object Composition & JAVA COLLECTIONS (ArrayList)
    // Order HAS-A collection of OrderItem objects.
    // Composition is demonstrated because order items are logically owned by their parent order.
    // ArrayList is chosen because the number of items per order is dynamically determined.
    private ArrayList<OrderItem> items;

    // OOP CONCEPT: Constructor
    public Order(int orderId, int customerId, String customerName, int farmerId, String farmerName, Date orderDate, double totalAmount, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = new ArrayList<OrderItem>();
    }

    // OOP CONCEPT: Constructor Overloading
    // For initializing new order before database persistence
    public Order(int customerId, int farmerId, OrderStatus status) {
        this(0, customerId, "", farmerId, "", new Date(), 0.0, status);
    }

    // COLLECTIONS / ARRAYLIST:
    // Adds an order line item and recalculates total amount
    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
            recalculateTotal();
        }
    }

    // ARITHMETIC OPERATIONS & METHODS:
    // Iterates through the collection of order items, summing the subtotal of each item
    public void recalculateTotal() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        this.totalAmount = sum;
    }

    // OOP CONCEPT: Encapsulation - Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
        recalculateTotal();
    }
}
