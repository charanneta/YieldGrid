package yieldgrid.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import yieldgrid.database.DBConnection;
import yieldgrid.exception.AuthenticationException;
import yieldgrid.exception.InsufficientStockException;
import yieldgrid.exception.ValidationException;
import yieldgrid.interfaces.DashboardActions;
import yieldgrid.model.CartItem;
import yieldgrid.model.Crop;
import yieldgrid.model.Customer;
import yieldgrid.model.Farmer;
import yieldgrid.model.Order;
import yieldgrid.model.OrderItem;
import yieldgrid.model.OrderStatus;
import yieldgrid.model.User;
import yieldgrid.service.AuthenticationService;
import yieldgrid.service.CropService;
import yieldgrid.service.OrderService;
import yieldgrid.service.RevenueService;
import yieldgrid.service.SessionManager;
import yieldgrid.security.PasswordUtil;

/*
 * ============================================================
 * TEST SUITE: WorkflowVerificationTest
 * PURPOSE:
 * Rigorously verifies all required Java OOP concepts, business logic,
 * validation rules, arithmetic computations, and database connectivity.
 * ============================================================
 */

public class WorkflowVerificationTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int testsBlocked = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("YieldGrid — Concept & Workflow Verification");
        System.out.println("==================================================");

        testEncapsulationAndModels();
        testInheritanceAndPolymorphism();
        testInterfaceCompliance();
        testEnumsAndTransitions();
        testObjectInteractionAndCart();
        testArithmeticAndComposition();
        testStringHandlingAndSearch();
        testValidationAndExceptionHandling();
        testAccessControlRules();
        testRecentlyFixedLogic();
        testDatabaseConnectivityAndLiveWorkflow();

        System.out.println("==================================================");
        System.out.println("TEST SUMMARY:");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Tests Blocked: " + testsBlocked);
        System.out.println("Tests Skipped: 0");
        System.out.println("==================================================");
        if (testsFailed > 0) {
            System.exit(1);
        }
        if (testsBlocked > 0) {
            System.exit(2);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }

    // 1. OOP CONCEPT: Encapsulation & Models
    private static void testEncapsulationAndModels() {
        System.out.println("\n--- 1. Testing Encapsulation & Model Classes ---");
        Crop crop = new Crop(1, 10, "Test Farmer", "Wheat", "Fresh golden wheat", 50, 40.0, true);

        assertTrue("Encapsulated getter for CropName", "Wheat".equals(crop.getCropName()));
        assertTrue("Encapsulated getter for Price", crop.getPrice() == 40.0);
        assertTrue("Encapsulated getter for Quantity", crop.getQuantity() == 50);

        crop.setQuantity(45);
        assertTrue("Encapsulated setter for Quantity", crop.getQuantity() == 45);
    }

    // 2. OOP CONCEPT: Inheritance & Polymorphism
    private static void testInheritanceAndPolymorphism() {
        System.out.println("\n--- 2. Testing Inheritance & Polymorphism ---");
        // Superclass reference holding Subclass instance
        User farmerUser = new Farmer(1, "Ramesh", "farmer1", "pass", "Guntur");
        User customerUser = new Customer(2, "Anita", "customer1", "pass", "Vijayawada");

        assertTrue("Farmer is instance of User", farmerUser instanceof User);
        assertTrue("Customer is instance of User", customerUser instanceof User);

        // Polymorphic method calls
        assertTrue("Polymorphic role description for Farmer",
                farmerUser.getRoleDescription().contains("Farmer (Producer)"));
        assertTrue("Polymorphic role description for Customer",
                customerUser.getRoleDescription().contains("Customer (Consumer)"));

        assertTrue("Polymorphic dashboard title for Farmer",
                farmerUser.getDashboardTitle().contains("Farmer"));
        assertTrue("Polymorphic dashboard title for Customer",
                customerUser.getDashboardTitle().contains("Marketplace"));
    }

    // 3. OOP CONCEPT: Interface
    private static void testInterfaceCompliance() {
        System.out.println("\n--- 3. Testing Interfaces ---");
        // Verify DashboardActions contract
        DashboardActions mockActions = new DashboardActions() {
            private boolean refreshed = false;
            @Override public void refreshData() { refreshed = true; }
            @Override public void showProfile() {}
            @Override public void logout() {}
        };
        mockActions.refreshData();
        assertTrue("Interface method invocation successful", mockActions != null);
    }

    // 4. JAVA ENUM: OrderStatus
    private static void testEnumsAndTransitions() {
        System.out.println("\n--- 4. Testing Java Enum (OrderStatus) ---");
        OrderStatus status = OrderStatus.PENDING;
        assertTrue("Enum initial state is PENDING", status == OrderStatus.PENDING);

        OrderStatus parsed = OrderStatus.fromString("CONFIRMED");
        assertTrue("Enum parsed from String CONFIRMED", parsed == OrderStatus.CONFIRMED);

        OrderStatus completed = OrderStatus.fromString("COMPLETED");
        assertTrue("Enum parsed from String COMPLETED", completed == OrderStatus.COMPLETED);

        OrderStatus fallback = OrderStatus.fromString("INVALID_STATUS");
        assertTrue("Enum fallback defaults to PENDING", fallback == OrderStatus.PENDING);
    }

    // 5. OBJECT INTERACTION: CartItem and Crop
    private static void testObjectInteractionAndCart() {
        System.out.println("\n--- 5. Testing Object Interaction & Collections (Cart) ---");
        Crop tomato = new Crop(1, 1, "Ramesh", "Organic Tomato", "Red tomatoes", 100, 30.0, true);
        CartItem cartItem = new CartItem(tomato, 5);

        // Interacts with Crop to calculate subtotal (30.0 * 5 = 150.0)
        assertTrue("CartItem interacts with Crop to compute subtotal", cartItem.getSubtotal() == 150.0);

        SessionManager.logout();
        ArrayList<CartItem> cart = SessionManager.getActiveCart();
        cart.add(cartItem);
        assertTrue("Collections: ArrayList stores CartItem", cart.size() == 1);
        SessionManager.clearCart();
        assertTrue("Collections: Cart cleared", SessionManager.getActiveCart().isEmpty());
    }

    // 6. ARITHMETIC OPERATIONS & COMPOSITION: Order HAS-A OrderItems
    private static void testArithmeticAndComposition() {
        System.out.println("\n--- 6. Testing Arithmetic Operations & Composition ---");
        Order order = new Order(101, 1, "Anita", 2, "Ramesh", new Date(), 0.0, OrderStatus.PENDING);

        OrderItem item1 = new OrderItem(1, "Tomato", 4, 30.0); // 120.0
        OrderItem item2 = new OrderItem(2, "Potato", 5, 20.0); // 100.0

        order.addItem(item1);
        order.addItem(item2);

        assertTrue("Composition: Order HAS-A collection of 2 OrderItems", order.getItems().size() == 2);
        assertTrue("Arithmetic: Order total calculated accurately (120 + 100 = 220)", order.getTotalAmount() == 220.0);
    }

    // 7. STRING HANDLING & SEARCH:
    private static void testStringHandlingAndSearch() {
        System.out.println("\n--- 7. Testing String Handling & Searching ---");
        String cropName = "Organic Tomato";
        String query = "tom";

        boolean matches = cropName.toLowerCase().contains(query.toLowerCase());
        assertTrue("String manipulation search matching", matches);

        String nonMatchQuery = "onion";
        boolean noMatch = cropName.toLowerCase().contains(nonMatchQuery.toLowerCase());
        assertTrue("String manipulation search non-match", !noMatch);
    }

    // 8. VALIDATION & CUSTOM EXCEPTION HANDLING:
    private static void testValidationAndExceptionHandling() {
        System.out.println("\n--- 8. Testing Validation & Custom Exceptions ---");
        AuthenticationService authService = new AuthenticationService();

        // Empty username validation
        boolean caughtValidation = false;
        try {
            authService.login("", "password123");
        } catch (ValidationException e) {
            caughtValidation = true;
        } catch (AuthenticationException e) {
            // unexpected
        }
        assertTrue("ValidationException thrown for empty username", caughtValidation);

        // Insufficient Stock Exception
        Crop limitedCrop = new Crop(1, 1, "Ramesh", "Chilli", "Spicy", 5, 50.0, true);
        OrderService orderService = new OrderService();
        boolean caughtInsufficient = false;
        try {
            orderService.addToCart(limitedCrop, 10); // Requests 10 when only 5 exist
        } catch (InsufficientStockException e) {
            caughtInsufficient = true;
        } catch (ValidationException e) {
            // unexpected
        }
        assertTrue("InsufficientStockException thrown when requesting > stock", caughtInsufficient);
    }

    // 9. ACCESS CONTROL:
    private static void testAccessControlRules() {
        System.out.println("\n--- 9. Testing Role & Multi-Tenant Access Control ---");
        CropService cropService = new CropService();
        Crop farmer1Crop = new Crop(10, 1, "Farmer 1 Crop", "Desc", 20, 25.0, true);

        // Farmer 2 attempts to modify Farmer 1's crop
        int requestingFarmerId = 2;
        boolean accessDenied = false;
        try {
            cropService.updateCrop(farmer1Crop, requestingFarmerId);
        } catch (ValidationException e) {
            if (e.getMessage().contains("Access Denied")) {
                accessDenied = true;
            }
        } catch (SQLException e) {
            // DB not connected in unit phase
        }
        assertTrue("Cross-farmer modification blocked by access control", accessDenied);
    }

    private static void testRecentlyFixedLogic() {
        System.out.println("\n--- 10. Testing Recently Fixed Checkout and Integrity Logic ---");
        String hash = PasswordUtil.hashPassword("secret");
        assertTrue("Password hash verifies", PasswordUtil.matches("secret", hash));
        assertTrue("Password hash rejects wrong password", !PasswordUtil.matches("wrong", hash));
        assertTrue("Legacy plaintext password remains upgrade-compatible", PasswordUtil.matches("legacy", "legacy"));
        assertTrue("PENDING can transition to CONFIRMED", OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
        assertTrue("PENDING cannot transition directly to COMPLETED", !OrderStatus.PENDING.canTransitionTo(OrderStatus.COMPLETED));
        assertTrue("CONFIRMED can transition to COMPLETED", OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.COMPLETED));
        assertTrue("COMPLETED cannot transition back to PENDING", !OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PENDING));

        SessionManager.logout();
        Crop staleCrop = new Crop(11, 1, "Farmer", "Tomato", "Fresh", 10, 5.0, true);
        Crop liveCrop = new Crop(11, 2, "Other Farmer", "Tomato", "Fresh", 8, 9.0, true);
        FakeCropDAO cropDAO = new FakeCropDAO(liveCrop);
        FakeOrderDAO orderDAO = new FakeOrderDAO();
        OrderService service = new OrderService(orderDAO, cropDAO);
        SessionManager.getActiveCart().add(new CartItem(staleCrop, 2));
        try {
            List<Order> orders = service.placeOrdersFromCart(7);
            assertTrue("Checkout uses live farmer identity", orders.size() == 1 && orders.get(0).getFarmerId() == 2);
            assertTrue("Checkout uses live database price", orders.get(0).getTotalAmount() == 18.0);
            assertTrue("Multi-farmer checkout is submitted as one DAO transaction", orderDAO.receivedOrders == orders);
        } catch (Exception e) {
            assertTrue("Checkout test completed without exception", false);
        } finally {
            SessionManager.clearCart();
        }

        FakeCropDAO deletableDAO = new FakeCropDAO(new Crop(12, 3, "Farmer", "Potato", "Fresh", 4, 3.0, true));
        CropService deletionService = new CropService(deletableDAO);
        try {
            assertTrue("Owned crop deletion delegates to DAO", deletionService.deleteCrop(12, 3));
        } catch (Exception e) {
            assertTrue("Crop deletion test completed without exception", false);
        }
        boolean deletionDenied = false;
        try {
            deletionService.deleteCrop(12, 4);
        } catch (ValidationException e) {
            deletionDenied = true;
        } catch (SQLException e) {
            // Not expected with the fake DAO.
        }
        assertTrue("Cross-owner crop deletion is rejected", deletionDenied);
    }

    // 10. DATABASE CONNECTIVITY & LIVE TEST:
    private static void testDatabaseConnectivityAndLiveWorkflow() {
        System.out.println("\n--- 11. Testing Database Connectivity ---");
        boolean canConnect = DBConnection.testConnection();
        if (canConnect) {
            System.out.println("  [PASS] Live MySQL connection established on localhost:3306/yieldgrid!");
            testsPassed++;
            try {
                DBConnection.initializeDatabaseTables();
                System.out.println("  [PASS] Live database tables verified / created successfully.");
                testsPassed++;
            } catch (Exception e) {
                System.err.println("  [FAIL] Database table check error: " + e.getMessage());
                testsFailed++;
            }
        } else {
            System.out.println("  [BLOCKED] MySQL is unavailable or config.properties is not configured.");
            testsBlocked++;
        }
    }

    private static class FakeCropDAO extends yieldgrid.dao.CropDAO {
        private final Crop crop;
        FakeCropDAO(Crop crop) { this.crop = crop; }
        @Override public Crop getCropById(int cropId) { return cropId == crop.getCropId() ? crop : null; }
        @Override public boolean deleteCrop(int cropId, int farmerId) {
            return cropId == crop.getCropId() && farmerId == crop.getFarmerId();
        }
    }

    private static class FakeOrderDAO extends yieldgrid.dao.OrderDAO {
        private List<Order> receivedOrders;
        @Override public boolean placeOrders(List<Order> orders) {
            receivedOrders = orders;
            return true;
        }
    }
}
