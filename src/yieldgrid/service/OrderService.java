package yieldgrid.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yieldgrid.dao.CropDAO;
import yieldgrid.dao.OrderDAO;
import yieldgrid.exception.InsufficientStockException;
import yieldgrid.exception.ValidationException;
import yieldgrid.model.CartItem;
import yieldgrid.model.Crop;
import yieldgrid.model.Order;
import yieldgrid.model.OrderItem;
import yieldgrid.model.OrderStatus;

/*
 * COURSE CONCEPT:
 * Object Composition, Object Interaction, Collections (ArrayList & Map), Arithmetic Operations, Enums, Exception Handling
 *
 * PURPOSE:
 * OrderService orchestrates customer cart operations, order placement calculations,
 * inventory stock validation, and order lifecycle state transitions.
 */

public class OrderService {

    private final OrderDAO orderDAO;
    private final CropDAO cropDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.cropDAO = new CropDAO();
    }

    public OrderService(OrderDAO orderDAO, CropDAO cropDAO) {
        this.orderDAO = orderDAO;
        this.cropDAO = cropDAO;
    }

    // OBJECT INTERACTION & COLLECTIONS:
    // Adds a crop to customer's active in-memory cart with quantity validation
    public void addToCart(Crop crop, int requestedQuantity) throws ValidationException, InsufficientStockException {
        if (crop == null) {
            throw new ValidationException("Selected crop cannot be null.");
        }
        if (requestedQuantity <= 0) {
            throw new ValidationException("Quantity to add must be greater than zero.");
        }

        // Check against current stock
        if (crop.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Cannot add " + requestedQuantity + " units. Only "
                    + crop.getQuantity() + " units are available in inventory.");
        }

        ArrayList<CartItem> cart = SessionManager.getActiveCart();

        // Check if crop already exists in cart; if so, update its quantity
        for (CartItem item : cart) {
            if (item.getCrop().getCropId() == crop.getCropId()) {
                int totalRequested = item.getQuantity() + requestedQuantity;
                if (crop.getQuantity() < totalRequested) {
                    throw new InsufficientStockException("Cart already has " + item.getQuantity()
                            + " units. Adding " + requestedQuantity + " exceeds available stock of "
                            + crop.getQuantity() + ".");
                }
                item.setQuantity(totalRequested);
                return;
            }
        }

        // Add new CartItem referencing Crop
        cart.add(new CartItem(crop, requestedQuantity));
    }

    // COLLECTIONS / ARRAYLIST:
    // Removes an item from the in-memory cart
    public void removeFromCart(int cropId) {
        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        cart.removeIf(item -> item.getCrop().getCropId() == cropId);
    }

    // ARITHMETIC OPERATION:
    // Updates quantity of an existing cart item
    public void updateCartQuantity(int cropId, int newQuantity) throws ValidationException, InsufficientStockException {
        if (newQuantity <= 0) {
            removeFromCart(cropId);
            return;
        }

        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        for (CartItem item : cart) {
            if (item.getCrop().getCropId() == cropId) {
                if (item.getCrop().getQuantity() < newQuantity) {
                    throw new InsufficientStockException("Requested quantity " + newQuantity
                            + " exceeds available stock of " + item.getCrop().getQuantity() + ".");
                }
                item.setQuantity(newQuantity);
                return;
            }
        }
    }

    // ARITHMETIC OPERATIONS & OBJECT INTERACTION:
    // Computes total price across all items currently in cart
    public double calculateCartTotal() {
        double total = 0.0;
        for (CartItem item : SessionManager.getActiveCart()) {
            // Interacts with CartItem and Crop to calculate subtotal
            total += item.getSubtotal();
        }
        return total;
    }

    // BUSINESS LOGIC & TRANSACTION COORDINATION:
    // Places orders for items in cart, grouping by farmer
    public List<Order> placeOrdersFromCart(int customerId)
            throws ValidationException, InsufficientStockException, SQLException {

        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Your cart is empty. Please add crops before checking out.");
        }

        // 1. Verify fresh live inventory and prices from the database
        Map<Integer, Crop> liveCrops = new HashMap<>();
        for (CartItem item : cart) {
            Crop liveCrop = cropDAO.getCropById(item.getCrop().getCropId());
            if (liveCrop == null || !liveCrop.isAvailable()) {
                throw new ValidationException("Crop '" + item.getCrop().getCropName() + "' is no longer available.");
            }
            if (liveCrop.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for '" + liveCrop.getCropName()
                        + "'. Only " + liveCrop.getQuantity() + " units available.");
            }
            liveCrops.put(liveCrop.getCropId(), liveCrop);
        }

        // 2. Group cart items by Farmer ID (Each order corresponds to one farmer)
        Map<Integer, List<CartItem>> itemsByFarmer = new HashMap<>();
        for (CartItem item : cart) {
            int farmerId = liveCrops.get(item.getCrop().getCropId()).getFarmerId();
            itemsByFarmer.computeIfAbsent(farmerId, k -> new ArrayList<>()).add(item);
        }

        List<Order> placedOrders = new ArrayList<>();

        // 3. Create and persist an Order for each farmer
        for (Map.Entry<Integer, List<CartItem>> entry : itemsByFarmer.entrySet()) {
            int farmerId = entry.getKey();
            List<CartItem> farmerItems = entry.getValue();

            // JAVA ENUM: Initial order state is PENDING
            Order order = new Order(customerId, farmerId, OrderStatus.PENDING);

            for (CartItem item : farmerItems) {
                Crop liveCrop = liveCrops.get(item.getCrop().getCropId());
                // OBJECT COMPOSITION: Create OrderItem from CartItem and Crop
                OrderItem orderItem = new OrderItem(
                    liveCrop.getCropId(),
                    liveCrop.getCropName(),
                    item.getQuantity(),
                    liveCrop.getPrice()
                );
                order.addItem(orderItem);
            }

            // Persist order and decrement stock atomically in DB
            placedOrders.add(order);
        }

        if (!orderDAO.placeOrders(placedOrders)) {
            throw new SQLException("Checkout transaction failed; no orders were committed.");
        }

        // 4. Clear the cart after successful order placement
        SessionManager.clearCart();
        return placedOrders;
    }

    // CRUD: READ
    public ArrayList<Order> getOrdersForCustomer(int customerId) throws SQLException {
        return orderDAO.getOrdersByCustomer(customerId);
    }

    // CRUD: READ
    public ArrayList<Order> getOrdersForFarmer(int farmerId) throws SQLException {
        return orderDAO.getOrdersByFarmer(farmerId);
    }

    // CRUD: UPDATE & ENUM
    // Farmer updates order status: PENDING -> CONFIRMED -> COMPLETED, or CANCELLED
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus, int requestingFarmerId)
            throws ValidationException, SQLException {

        if (newStatus == null) {
            throw new ValidationException("Target status cannot be null.");
        }

        // The DAO query also enforces ownership and the lifecycle transition.
        return orderDAO.updateOrderStatus(orderId, newStatus, requestingFarmerId);
    }
}
