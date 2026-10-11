# YieldGrid — Java OOP Concept Mapping

**Project:** YieldGrid (Application for Farm-to-Retail Inventory)

---

## 1. Overview
This document maps the Java and OOP concepts demonstrated by each module, source file, class, and method in YieldGrid.

---

## 2. Comprehensive Concept Mapping Table

| # | Java Course Topic | Project Module | Class / File | Method / Location | How It Is Demonstrated in YieldGrid |
|---|---|---|---|---|---|
| **1** | **Classes** | Model & Service | `User.java`, `Crop.java`, `Order.java` | Entire class definitions | Defines blueprints representing real-world farm-to-retail entities with state and behavior. |
| **2** | **Objects** | Runtime Session & DAO | `Main.java`, `UserDAO.java`, `CartPanel.java` | Object instantiation via `new` | Instantiates runtime representations of `Farmer`, `Customer`, `Crop`, and `CartItem` during user workflows. |
| **3** | **Encapsulation** | Model Layer | `User.java`, `Crop.java`, `OrderItem.java` | Private fields + public getters/setters | All entity state variables are declared `private`; access and updates are strictly controlled via public accessors and mutators. |
| **4** | **Inheritance** | User Management | `Farmer.java`, `Customer.java` | `extends User` | `Farmer` and `Customer` extend abstract `User`, inheriting shared authentication and profile attributes while eliminating duplicate code. |
| **5** | **Interfaces** | GUI Lifecycle | `DashboardActions.java` | `FarmerDashboard`, `CustomerDashboard` | Defines uniform navigation and lifecycle operations (`refreshData()`, `showProfile()`, `logout()`) across different dashboard types. |
| **6** | **Validation** | Business Services | `AuthenticationService.java`, `CropService.java` | `login()`, `register()`, `addCrop()` | Checks for non-empty fields, minimum string length, unique usernames, positive stock quantities, and positive unit prices. |
| **7** | **Exception Handling** | Custom Exceptions & Services | `ValidationException.java`, `InsufficientStockException.java`, `AuthenticationException.java` | `try-catch-finally`, `throw`, `throws` | Employs checked custom exceptions to gracefully trap domain errors without crashing GUI or corrupting state. |
| **8** | **Collections** | Cart & Multi-item Orders | `SessionManager.java`, `OrderService.java` | `ArrayList<CartItem>`, `Map<Integer, List<CartItem>>` | Maintains dynamic lists of items in shopping cart and groups items by farmer during order placement. |
| **9** | **ArrayList** | Inventory & Orders | `Order.java`, `CropDAO.java`, `OrdersPanel.java` | `ArrayList<OrderItem>`, `ArrayList<Crop>` | Used to store and iterate over dynamically sized sets of crops, cart items, and incoming orders retrieved from MySQL. |
| **10** | **String Handling** | Catalog Search & Parsing | `CropBrowsePanel.java`, `OrderStatus.java` | `applySearchFilter()`, `fromString()` | Performs case-insensitive searches (`toLowerCase()`, `contains()`) and string trimming / parsing for status and search queries. |
| **11** | **Object Interaction** | Shopping Cart | `CartItem.java`, `OrderService.java` | `getSubtotal()` | `CartItem` interacts with `Crop` instance to read price and verify available stock quantity dynamically. |
| **12** | **Object Relationships (Composition)** | Order Processing | `Order.java`, `OrderItem.java` | `Order HAS-A ArrayList<OrderItem>` | An `Order` owns and contains multiple `OrderItem` objects, modeling a real-world multi-item purchase. |
| **13** | **Enums** | Order Tracking | `OrderStatus.java` | `enum OrderStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }` | Restricts order states to a strictly typed, immutable set of valid states, preventing invalid string values. |
| **14** | **Conditional Logic** | Status & Access Control | `OrdersPanel.java`, `CropService.java` | `updateCrop()`, `handleLogin()` | Checks farmer ownership (`farmerId == requestingFarmerId`) and controls visible actions based on active `OrderStatus`. |
| **15** | **Methods** | Reusability & Logic | `OrderService.java`, `RevenueService.java` | `calculateCartTotal()`, `calculateFarmerRevenue()` | Encapsulates reusable business logic into modular methods to avoid duplicating calculation logic across UI components. |
| **16** | **Arithmetic Operations** | Cart & Revenue | `CartItem.java`, `Order.java`, `RevenuePanel.java` | Subtotal, Cart Total, Revenue Sum | Multiplies quantity by unit price (`price * quantity`), sums item totals, and calculates total farmer revenue from completed orders. |
| **17** | **Java Swing** | Presentation Layer | `LoginFrame.java`, `FarmerDashboard.java`, `CustomerDashboard.java` | `JFrame`, `JPanel`, `JTable`, `JTabbedPane`, `JSpinner`, `JOptionPane` | Complete desktop GUI implementation without any third-party GUI or web frameworks. |
| **18** | **Event Handling** | Interactive GUI | UI Panels & Frames | `ActionListener`, `ListSelectionListener`, `ChangeListener` | Connects button clicks, table selections, and tab transitions to back-end service invocations. |
| **19** | **Layout Managers** | Responsive UI Layout | All UI Classes | `BorderLayout`, `GridBagLayout`, `FlowLayout`, `GridLayout` | Organizes graphical components into proportional, adaptable panels that resize cleanly across different screen dimensions. |
| **20** | **JDBC** | Database Integration | `DBConnection.java` | `DriverManager.getConnection()` | Connects Java application to local MySQL server using official MySQL Connector/J driver. |
| **21** | **SQL & CRUD (Create)** | Database Persistence | `UserDAO.java`, `CropDAO.java`, `OrderDAO.java` | `INSERT INTO ...` | Creates persistent records for users, crops, orders, and order items. |
| **22** | **SQL & CRUD (Read)** | Data Retrieval | `CropDAO.java`, `OrderDAO.java` | `SELECT ... FROM ... JOIN ... WHERE` | Queries MySQL database to fetch active crops, customer order history, and farmer incoming orders. |
| **23** | **SQL & CRUD (Update)** | Inventory & Status | `CropDAO.java`, `OrderDAO.java` | `UPDATE crops ...`, `UPDATE orders ...` | Modifies crop prices/quantities and advances order status (`PENDING` -> `CONFIRMED` -> `COMPLETED`). |
| **24** | **SQL & CRUD (Delete)** | Inventory Removal | `CropDAO.java` | `DELETE FROM crops WHERE crop_id = ? AND farmer_id = ?` | Permanently removes selected crop records with multi-tenant ownership verification. |
| **25** | **PreparedStatement** | Safe Parameterized SQL | All DAO Classes | `conn.prepareStatement(sql)` | Safeguards database queries against SQL injection attacks by separating SQL syntax from user inputs. |
| **26** | **ResultSet** | Database Retrieval | All DAO Classes | `rs.getInt()`, `rs.getString()`, `rs.getDouble()` | Maps tabular SQL query output into in-memory Java model instances. |
| **27** | **JDBC Transactions** | Data Consistency | `OrderDAO.java` | `conn.setAutoCommit(false)`, `commit()`, `rollback()` | Atomic order placement: simultaneously inserts order, line items, and deducts crop stock, rolling back if stock is insufficient. |
| **28** | **Polymorphism** | Dynamic Method Dispatch | `UserDAO.java`, `ProfilePanel.java` | `User user = ...; user.getRoleDescription();` | Handles `Farmer` and `Customer` uniformly through `User` superclass reference while dynamically executing overridden subclass methods. |

---

## 3. Quick Questions & Answers

### Q1: Why is `User` declared as an `abstract` class?
**Answer:** In YieldGrid, every registered user is either a `Farmer` (producer) or a `Customer` (buyer). No generic user can exist on their own. Making `User` abstract prevents direct instantiation and forces concrete subclasses to implement polymorphic methods like `getRoleDescription()` and `getDashboardTitle()`.

### Q2: How is Encapsulation maintained throughout the project?
**Answer:** All instance variables across all model classes (`User`, `Crop`, `CartItem`, `Order`, `OrderItem`) are marked `private`. External classes cannot alter them directly and must use public getters and setters that enforce validation rules.

### Q3: What is the difference between `CartItem` and `OrderItem`?
**Answer:** `CartItem` is an in-memory, transient object representing a customer's active shopping selection. It directly references a live `Crop` object. Once an order is placed, an `OrderItem` is permanently persisted in the MySQL database, recording the exact historical price and quantity at the time of purchase, preserving data integrity even if the farmer later changes the crop's listing price.

### Q4: Why use an `enum` for `OrderStatus` instead of `String`?
**Answer:** An `enum` provides compile-time type safety. It ensures that an order can only ever have one of the four defined states (`PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`). This eliminates typos, prevents invalid states, and simplifies `switch` / `if` statements.

### Q5: How is inventory stock kept consistent during order placement?
**Answer:** In `OrderDAO.placeOrder()`, a JDBC transaction is initiated with `conn.setAutoCommit(false)`. The order header is inserted, line items are inserted, and crop quantities are decremented. If any item has insufficient stock during the transaction, `conn.rollback()` is invoked and an `InsufficientStockException` is thrown, ensuring inventory is never corrupted or made negative.

### Q6: How does the application prevent hardcoded database passwords?
**Answer:** Database connection settings are externalized to `config.properties`. `DBConnection.java` reads the URL, username, and password at runtime using `java.util.Properties`. `config.properties` is listed in `.gitignore` to prevent credentials from being exposed in version control.
