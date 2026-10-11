package yieldgrid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import yieldgrid.database.DBConnection;
import yieldgrid.model.Order;
import yieldgrid.model.OrderItem;
import yieldgrid.model.OrderStatus;
import java.util.List;

/*
 * COURSE CONCEPT:
 * JDBC Transactions, SQL CRUD, PreparedStatement, ResultSet, Collections (ArrayList), Arithmetic Summation
 *
 * PURPOSE:
 * OrderDAO coordinates persistent storage and retrieval for customer orders and items.
 * It uses JDBC transaction management (commit/rollback) to ensure inventory deduction
 * and order creation occur atomically without stock corruption.
 */

public class OrderDAO {

    // JDBC TRANSACTION CONCEPT:
    // Creates the order record, line items, and updates crop inventory within a single atomic transaction
    public boolean placeOrder(Order order) throws SQLException {
        return placeOrders(java.util.Collections.singletonList(order));
    }

    public boolean placeOrders(List<Order> orders) throws SQLException {
        if (orders == null || orders.isEmpty()) {
            return false;
        }
        String insertOrderSql = "INSERT INTO orders (customer_id, farmer_id, total_amount, status) VALUES (?, ?, ?, ?);";
        String insertItemSql = "INSERT INTO order_items (order_id, crop_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?);";
        String updateStockSql = "UPDATE crops SET quantity = quantity - ?, available = (quantity - ?) > 0 WHERE crop_id = ? AND quantity >= ?;";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // JDBC CONCEPT: Begin Transaction
            conn.setAutoCommit(false);

            try (PreparedStatement itemPstmt = conn.prepareStatement(insertItemSql);
                 PreparedStatement stockPstmt = conn.prepareStatement(updateStockSql);
                 PreparedStatement orderPstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                for (Order order : orders) {
                    if (order == null || order.getItems().isEmpty()) {
                        conn.rollback();
                        return false;
                    }
                    orderPstmt.setInt(1, order.getCustomerId());
                    orderPstmt.setInt(2, order.getFarmerId());
                    orderPstmt.setDouble(3, order.getTotalAmount());
                    orderPstmt.setString(4, order.getStatus().name());
                    if (orderPstmt.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                    try (ResultSet rs = orderPstmt.getGeneratedKeys()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        int orderId = rs.getInt(1);
                        order.setOrderId(orderId);
                        for (OrderItem item : order.getItems()) {
                            item.setOrderId(orderId);
                            itemPstmt.setInt(1, orderId);
                            itemPstmt.setInt(2, item.getCropId());
                            itemPstmt.setInt(3, item.getQuantity());
                            itemPstmt.setDouble(4, item.getUnitPrice());
                            itemPstmt.setDouble(5, item.getSubtotal());
                            itemPstmt.executeUpdate();

                            stockPstmt.setInt(1, item.getQuantity());
                            stockPstmt.setInt(2, item.getQuantity());
                            stockPstmt.setInt(3, item.getCropId());
                            stockPstmt.setInt(4, item.getQuantity());
                            if (stockPstmt.executeUpdate() == 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }
                }
            }

            // JDBC CONCEPT: Commit Transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    // JDBC CONCEPT: Rollback on Error
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Connection close error: " + e.getMessage());
                }
            }
        }
    }

    // CRUD CONCEPT: READ (SELECT ORDERS BY CUSTOMER)
    // Retrieves order history for a specific customer
    public ArrayList<Order> getOrdersByCustomer(int customerId) throws SQLException {
        String sql = "SELECT o.order_id, o.customer_id, u_cust.name AS customer_name, "
                   + "o.farmer_id, u_farm.name AS farmer_name, o.order_date, o.total_amount, o.status "
                   + "FROM orders o "
                   + "JOIN users u_cust ON o.customer_id = u_cust.user_id "
                   + "JOIN users u_farm ON o.farmer_id = u_farm.user_id "
                   + "WHERE o.customer_id = ? "
                   + "ORDER BY o.order_id DESC;";

        ArrayList<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("farmer_id"),
                        rs.getString("farmer_name"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        OrderStatus.fromString(rs.getString("status"))
                    );
                    // Populate order items
                    order.setItems(getOrderItems(order.getOrderId()));
                    list.add(order);
                }
            }
        }
        return list;
    }

    // CRUD CONCEPT: READ (SELECT ORDERS BY FARMER)
    // Retrieves incoming orders received by a specific farmer
    public ArrayList<Order> getOrdersByFarmer(int farmerId) throws SQLException {
        String sql = "SELECT o.order_id, o.customer_id, u_cust.name AS customer_name, "
                   + "o.farmer_id, u_farm.name AS farmer_name, o.order_date, o.total_amount, o.status "
                   + "FROM orders o "
                   + "JOIN users u_cust ON o.customer_id = u_cust.user_id "
                   + "JOIN users u_farm ON o.farmer_id = u_farm.user_id "
                   + "WHERE o.farmer_id = ? "
                   + "ORDER BY o.order_id DESC;";

        ArrayList<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("farmer_id"),
                        rs.getString("farmer_name"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        OrderStatus.fromString(rs.getString("status"))
                    );
                    order.setItems(getOrderItems(order.getOrderId()));
                    list.add(order);
                }
            }
        }
        return list;
    }

    // CRUD CONCEPT: READ (SELECT ITEMS FOR ORDER)
    public ArrayList<OrderItem> getOrderItems(int orderId) throws SQLException {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.crop_id, c.crop_name, "
                   + "oi.quantity, oi.unit_price, oi.subtotal "
                   + "FROM order_items oi "
                   + "LEFT JOIN crops c ON oi.crop_id = c.crop_id "
                   + "WHERE oi.order_id = ?;";

        ArrayList<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("crop_name");
                    if (name == null || name.isEmpty()) {
                        name = "Crop #" + rs.getInt("crop_id");
                    }
                    OrderItem item = new OrderItem(
                        rs.getInt("order_item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("crop_id"),
                        name,
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal")
                    );
                    items.add(item);
                }
            }
        }
        return items;
    }

    // CRUD CONCEPT: UPDATE (ORDER STATUS)
    // Farmer updates status: PENDING -> CONFIRMED -> COMPLETED, or CANCELLED
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus, int farmerId) throws SQLException {
        if (newStatus == null) {
            return false;
        }
        String sql = "UPDATE orders SET status = ? "
                   + "WHERE order_id = ? AND farmer_id = ? "
                   + "AND ((status = 'PENDING' AND ? IN ('CONFIRMED', 'CANCELLED')) "
                   + "OR (status = 'CONFIRMED' AND ? IN ('COMPLETED', 'CANCELLED')));";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, farmerId);
            pstmt.setString(4, newStatus.name());
            pstmt.setString(5, newStatus.name());

            return pstmt.executeUpdate() > 0;
        }
    }

    // ARITHMETIC OPERATIONS & DATABASE RETRIEVAL:
    // Calculates total revenue from COMPLETED orders for the given farmer
    public double getTotalRevenueByFarmer(int farmerId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0.0) AS total_revenue "
                   + "FROM orders WHERE farmer_id = ? AND status = 'COMPLETED';";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_revenue");
                }
            }
        }
        return 0.0;
    }

    // ARITHMETIC OPERATIONS & DATABASE RETRIEVAL:
    // Counts completed orders for a farmer
    public int getCompletedOrdersCountByFarmer(int farmerId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_count "
                   + "FROM orders WHERE farmer_id = ? AND status = 'COMPLETED';";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, farmerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_count");
                }
            }
        }
        return 0;
    }
}
