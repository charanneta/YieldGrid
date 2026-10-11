# YieldGrid — Application for Farm-to-Retail Inventory

---

## 1. Project Title & Subtitle
**YieldGrid** — *Application for Farm-to-Retail Inventory*

---

## 2. Project Objective
YieldGrid is a desktop-based Direct-to-Consumer (D2C) farm-to-retail inventory management application built using pure **Java Swing** and **JDBC with MySQL**.

The system bridges local agricultural producers (Farmers) and retail consumers (Customers) by eliminating middlemen. Farmers can list crops, manage prices, monitor inventory, process incoming orders, and track revenue. Customers can browse farm-fresh produce, maintain an active shopping cart, place direct orders, and track fulfillment status.

The project is designed as a focused desktop application that visibly demonstrates core Object-Oriented Programming (OOP) concepts, GUI development, and relational database operations.

---

## 3. Features

### Farmer Module
- **User Authentication:** Registration and login with role validation.
- **Farmer Dashboard:** Tabbed interface for inventory, orders, revenue, and profile.
- **Crop Management (CRUD):**
  - **Create:** Add crops with title, description, stock quantity, and unit price.
  - **Read:** View listed crops in an interactive `JTable`.
  - **Update:** Modify crop pricing, available stock, and sale availability.
  - **Delete:** Remove crop listings with multi-tenant ownership enforcement.
- **Order Management:**
  - View incoming orders placed by customers.
  - Inspect line items (crops, quantities, unit prices, and subtotals).
  - Update order status: `PENDING` -> `CONFIRMED` -> `COMPLETED`, or `CANCELLED`.
- **Revenue Tracking:**
  - Automatic arithmetic calculation of total realized revenue from `COMPLETED` orders.
  - Metrics display showing completed orders count and financial ledger breakdown.
- **Profile:** Inspect authenticated farmer credentials and farm location details.

### Customer Module
- **User Authentication:** Registration and login for retail customers.
- **Customer Dashboard:** Tabbed interface for browsing, cart, orders, and profile.
- **Marketplace Browsing:**
  - View all active crops listed across farmers.
  - Search produce by crop name, farm/farmer name, or description using real-time string filtering.
- **Shopping Cart:**
  - Add crops to cart with numerical quantity selection via `JSpinner`.
  - Validate stock availability dynamically before adding.
  - Modify quantities or remove items from cart.
  - Automatic arithmetic calculation of cart subtotals and grand total.
- **Order Placement:**
  - Transactional order placement grouping items by farmer.
  - Automatic deduction of crop stock in database and prevention of negative inventory.
  - Clear cart upon successful checkout.
- **Order Tracking & History:**
  - View past and active orders with live `OrderStatus`.
  - Inspect line items of each order.
- **Profile:** View customer account details and delivery address.

---

## 4. User Roles
There are strictly **two user roles**:
1. **FARMER (Producer):** Lists produce, manages inventory, fulfills incoming orders, and tracks revenue.
2. **CUSTOMER (Consumer):** Explores available crops, manages shopping cart, places orders, and tracks fulfillment.

---

## 5. Technology Stack
- **Language:** Java (JDK 17 or later, compatible with JDK 21/26)
- **GUI Toolkit:** Java Swing & AWT (pure desktop GUI)
- **Database:** MySQL Server (8.0+ / 8.4+ / 26.7+)
- **Database Connectivity:** JDBC (MySQL Connector/J 9.2.0 driver)
- **Architecture:** 3-Tier Layered Architecture (Presentation -> Service/Business Logic -> Data Access Object -> Database)

---

## 6. Project Folder Structure

```text
YieldGrid/
├── src/
│   └── yieldgrid/
│       ├── main/
│       │   └── Main.java                     # Application bootstrap and EDT launcher
│       ├── model/
│       │   ├── User.java                     # Abstract base class for users
│       │   ├── Farmer.java                   # Concrete User subclass for Farmers
│       │   ├── Customer.java                 # Concrete User subclass for Customers
│       │   ├── Crop.java                     # Crop inventory entity
│       │   ├── CartItem.java                 # Transient shopping cart item
│       │   ├── Order.java                    # Purchase order header entity
│       │   ├── OrderItem.java                # Order line item entity
│       │   └── OrderStatus.java              # Java enum for order lifecycle states
│       ├── interfaces/
│       │   └── DashboardActions.java         # Lifecycle interface for dashboards
│       ├── dao/
│       │   ├── UserDAO.java                  # User database operations & auth
│       │   ├── CropDAO.java                  # Crop CRUD database operations
│       │   └── OrderDAO.java                 # Order transaction & revenue operations
│       ├── exception/
│       │   ├── ValidationException.java      # Checked exception for invalid input
│       │   ├── InsufficientStockException.java # Checked exception for inventory shortage
│       │   └── AuthenticationException.java  # Checked exception for login failures
│       ├── service/
│       │   ├── AuthenticationService.java   # Auth validation and session creation
│       │   ├── CropService.java             # Crop business rules and authorization
│       │   ├── OrderService.java            # Cart logic, stock checks, and checkout
│       │   ├── RevenueService.java          # Revenue arithmetic and order filtering
│       │   └── SessionManager.java          # Current user session and in-memory cart
│       ├── ui/
│       │   ├── UITheme.java                 # Color palette, fonts, and styling helpers
│       │   ├── LoginFrame.java              # Login GUI window
│       │   ├── RegisterFrame.java           # Account creation GUI window
│       │   ├── FarmerDashboard.java         # Farmer workspace frame
│       │   ├── CustomerDashboard.java       # Customer shopping frame
│       │   ├── CropManagementPanel.java     # Farmer crop CRUD panel
│       │   ├── CropBrowsePanel.java         # Customer marketplace panel
│       │   ├── CartPanel.java               # Customer shopping cart panel
│       │   ├── OrdersPanel.java             # Shared orders tracking & status panel
│       │   ├── ProfilePanel.java            # User profile viewer panel
│       │   └── RevenuePanel.java            # Farmer sales and revenue panel
│       └── test/
│           └── WorkflowVerificationTest.java # Automated verification test suite
├── database/
│   └── yieldgrid.sql                        # Schema creation and sample seed data
├── lib/
│   └── mysql-connector-j-9.2.0.jar          # Official MySQL JDBC driver
├── bin/                                     # Compiled bytecode (.class files)
├── config.properties                        # Local MySQL connection configuration
├── config.example.properties                # Configuration template
├── COURSE_CONCEPT_MAP.md                    # Java OOP concept mapping
├── Implementation.md                        # Master project specification
├── .gitignore                               # Git ignore configuration
├── compile.bat                              # One-click compile script (Windows)
└── run.bat                                  # One-click launch script (Windows)
```

---

## 7. Database Schema

The database consists of 4 normalized relational tables in the `yieldgrid` database:

### `users` Table
| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(100) | NOT NULL | User's full name or farm name |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Login credential |
| `password` | VARCHAR(100) | NOT NULL | Password |
| `role` | VARCHAR(20) | NOT NULL | 'FARMER' or 'CUSTOMER' |
| `contact_info` | VARCHAR(150) | | Phone number and location |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Registration timestamp |

### `crops` Table
| Column | Type | Constraints | Description |
|---|---|---|---|
| `crop_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique crop identifier |
| `farmer_id` | INT | NOT NULL, FOREIGN KEY (`users`) | Farmer who owns the crop |
| `crop_name` | VARCHAR(100) | NOT NULL | Name of agricultural produce |
| `description` | TEXT | | Variety / harvesting notes |
| `quantity` | INT | NOT NULL | Available inventory in stock |
| `price` | DECIMAL(10,2) | NOT NULL | Unit price in INR (Rs.) |
| `available` | BOOLEAN | DEFAULT TRUE | Listing status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Listing timestamp |

### `orders` Table
| Column | Type | Constraints | Description |
|---|---|---|---|
| `order_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique order identifier |
| `customer_id` | INT | NOT NULL, FOREIGN KEY (`users`) | Customer who placed order |
| `farmer_id` | INT | NOT NULL, FOREIGN KEY (`users`) | Farmer fulfilling order |
| `order_date` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Placement timestamp |
| `total_amount` | DECIMAL(10,2) | NOT NULL | Grand order total (Rs.) |
| `status` | VARCHAR(20) | DEFAULT 'PENDING' | Enum: PENDING, CONFIRMED, COMPLETED, CANCELLED |

### `order_items` Table
| Column | Type | Constraints | Description |
|---|---|---|---|
| `order_item_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Line item identifier |
| `order_id` | INT | NOT NULL, FOREIGN KEY (`orders`) | Parent order reference |
| `crop_id` | INT | NOT NULL, FOREIGN KEY (`crops`) | Ordered crop reference |
| `quantity` | INT | NOT NULL | Number of units purchased |
| `unit_price` | DECIMAL(10,2) | NOT NULL | Historical price at order time |
| `subtotal` | DECIMAL(10,2) | NOT NULL | `quantity * unit_price` |

---

## 8. Setup Instructions

### Prerequisites
1. **Java Development Kit (JDK 17 or later):** Ensure `java` and `javac` are available in system PATH.
2. **MySQL Server:** Ensure local MySQL server is installed and running on port 3306.

### Step 1: Configure Database Credentials
Create `config.properties` in the project root by copying `config.example.properties`:
```properties
# Template format from config.example.properties:
db.url=jdbc:mysql://localhost:3306/yieldgrid
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```
Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL `root` password.

*(Note: `config.properties` is protected by `.gitignore` so your actual password is never committed to GitHub. `config.example.properties` is committed as the reference template.)*

Passwords are stored as salted PBKDF2 hashes. Existing legacy accounts are
verified once at login and upgraded automatically; see
`database/migrate_passwords_and_crop_fk.sql` before upgrading an existing
database.

### Step 2: Seed Database (Optional)
The application will automatically verify and create missing tables on first startup. However, you can also import `database/yieldgrid.sql` manually via MySQL Workbench or command line:
```bash
mysql -u root -p < database/yieldgrid.sql
```

The application seeds the built-in demonstration users with password hashes
when the users table is empty. Do not copy passwords into source files or
commit local `config.properties`.

---

## 9. How to Compile and Run

### Option A: Using Windows Batch Scripts
- Double-click `compile.bat` (or run in terminal) to compile all source files into `bin/`.
- Double-click `run.bat` (or run in terminal) to launch the Swing application.

### Option B: Using PowerShell / Command Prompt Manually
**Compile:**
```powershell
javac -cp "lib/mysql-connector-j-9.2.0.jar" -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

**Run Application:**
```powershell
java -cp "bin;lib/mysql-connector-j-9.2.0.jar" yieldgrid.main.Main
```

**Run Automated Verification Test Suite:**
```powershell
java -cp "bin;lib/mysql-connector-j-9.2.0.jar" yieldgrid.test.WorkflowVerificationTest
```

---

## 10. Sample Demo Credentials

| Role | Name / Farm | Username | Password | Notes |
|---|---|---|---|---|
| **Farmer** | Ramesh Kumar (Green Valley Farms) | `farmer1` | Set during local initialization | Has pre-seeded crops: Tomato, Potato, Onion |
| **Farmer** | Suresh Patel (Sunshine Organic) | `farmer2` | Set during local initialization | Has pre-seeded crops: Carrot, Spinach, Chilli |
| **Customer** | Anita Sharma | `customer1` | Set during local initialization | Can browse, add to cart, and place orders |
| **Customer** | Vikram Reddy | `customer2` | Set during local initialization | Separate customer account |

*(You can also use the **Register** button on the login screen to create custom accounts.)*

---

## 11. Java OOP Concept Mapping

Key concepts demonstrated:
- **Encapsulation:** Private fields, public getters and setters across all model entities.
- **Inheritance:** `Farmer extends User` and `Customer extends User`.
- **Polymorphism:** Abstract methods `getRoleDescription()` and `getDashboardTitle()`; superclass `User` references.
- **Interfaces:** `DashboardActions` implemented by `FarmerDashboard` and `CustomerDashboard`.
- **Java Enum:** `OrderStatus` (`PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`).
- **Collections & ArrayList:** `ArrayList<CartItem>`, `ArrayList<Crop>`, `ArrayList<OrderItem>`.
- **Object Relationships:** Composition (`Order HAS-A ArrayList<OrderItem>`) and interaction (`CartItem -> Crop`).
- **Custom Exceptions:** `ValidationException`, `InsufficientStockException`, `AuthenticationException`.
- **String Handling:** Search filter matching using `.toLowerCase()` and `.contains()`.
- **Arithmetic Operations:** Cart total calculation, subtotal derivation, stock deduction, and revenue summation.
- **Swing GUI:** `JFrame`, `JPanel`, `JTable`, `JTabbedPane`, `JSpinner`, `JOptionPane`, `JScrollPane`.
- **Event Handling:** `ActionListener`, `ListSelectionListener`, `ChangeListener`.
- **Layout Managers:** `BorderLayout`, `GridBagLayout`, `FlowLayout`, `GridLayout`.
- **JDBC & MySQL:** `Connection`, `PreparedStatement`, `ResultSet`, atomic transactions (`commit`/`rollback`).
- **CRUD:** Complete Create, Read, Update, and Delete operations for crops and orders.

---

## 12. Scope Discipline & Out-of-Scope Features
To keep the project focused and maintainable, the following elements are intentionally excluded:
- No AI/ML/LLM integrations or disease detection.
- No payment gateway integrations (orders are settled farm-to-retail directly).
- No web/mobile frameworks (React, Angular, Spring Boot, JavaFX).
- No SMS/OTP third-party authentication services.
- No GPS live delivery vehicle tracking.
- No complex multi-vendor admin portals.
