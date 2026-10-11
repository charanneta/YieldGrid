package yieldgrid.service;

import java.sql.SQLException;
import java.util.ArrayList;
import yieldgrid.dao.OrderDAO;
import yieldgrid.model.Order;
import yieldgrid.model.OrderStatus;

/*
 * COURSE CONCEPT:
 * Methods / Reusability, Arithmetic Operations, Database Retrieval, Collections (ArrayList)
 *
 * PURPOSE:
 * RevenueService encapsulates financial calculations for farmers in YieldGrid.
 * It retrieves completed orders and calculates total earned revenue.
 */

public class RevenueService {

    private final OrderDAO orderDAO;

    public RevenueService() {
        this.orderDAO = new OrderDAO();
    }

    public RevenueService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    // ARITHMETIC OPERATIONS & DATABASE RETRIEVAL:
    // Calculates total earnings by querying database for all COMPLETED orders belonging to the farmer
    public double calculateFarmerRevenue(int farmerId) throws SQLException {
        return orderDAO.getTotalRevenueByFarmer(farmerId);
    }

    // DATABASE RETRIEVAL:
    // Retrieves count of fulfilled orders
    public int getCompletedOrdersCount(int farmerId) throws SQLException {
        return orderDAO.getCompletedOrdersCountByFarmer(farmerId);
    }

    // COLLECTIONS / ARRAYLIST & CONDITIONAL FILTERING:
    // Returns the list of completed orders for detailed revenue breakdown display
    public ArrayList<Order> getCompletedOrders(int farmerId) throws SQLException {
        ArrayList<Order> allOrders = orderDAO.getOrdersByFarmer(farmerId);
        ArrayList<Order> completedList = new ArrayList<>();

        for (Order o : allOrders) {
            // CONDITIONAL LOGIC: Only COMPLETED orders contribute to realized revenue
            if (o.getStatus() == OrderStatus.COMPLETED) {
                completedList.add(o);
            }
        }
        return completedList;
    }
}
