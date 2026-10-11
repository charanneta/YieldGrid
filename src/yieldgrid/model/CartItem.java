package yieldgrid.model;

/*
 * COURSE CONCEPT:
 * Object Interaction, Encapsulation, Arithmetic Operations
 *
 * PURPOSE:
 * CartItem models a temporary selection in a customer's shopping cart.
 * It references a Crop object and specifies the desired quantity to purchase.
 */

public class CartItem {

    // OBJECT RELATIONSHIP & INTERACTION:
    // CartItem HAS-A reference to the Crop object being purchased.
    // It queries the Crop for current price, title, and stock availability.
    private Crop crop;
    private int quantity;

    // OOP CONCEPT: Constructor
    public CartItem(Crop crop, int quantity) {
        this.crop = crop;
        this.quantity = quantity;
    }

    // OOP CONCEPT: Encapsulation - Getters and Setters
    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ARITHMETIC OPERATIONS & OBJECT INTERACTION:
    // Calculates line item cost by interacting with the Crop's unit price
    public double getSubtotal() {
        return crop.getPrice() * quantity;
    }

    // ARITHMETIC OPERATION:
    // Increases quantity by adding additional units
    public void addQuantity(int additionalQuantity) {
        if (additionalQuantity > 0) {
            this.quantity += additionalQuantity;
        }
    }
}
