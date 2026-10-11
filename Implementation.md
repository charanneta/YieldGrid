PROJECT:
YieldGrid

SUBTITLE:
Application for Farm-to-Retail Inventory

============================================================
1. PROJECT CONSTRAINTS
============================================================

This is a focused desktop application, not a startup product or a production-scale platform.

The target is:

- larger than a small Java program
- smaller than a production-scale application
- fully runnable
- easy to explain and maintain
- clearly demonstrates Object-Oriented Programming
- clearly demonstrates Java GUI development
- clearly demonstrates JDBC and MySQL
- logically structured
- efficient enough for a desktop application
- complete enough to demonstrate the defined workflow
- NOT overloaded with unnecessary functionality

DO NOT turn this into:
- a startup
- a hackathon project
- a SaaS platform
- an AI platform
- a full e-commerce platform
- a logistics management system
- an enterprise architecture
- a microservices project

The implementation must stay strictly within the scope defined below.

============================================================
2. AUTHORITATIVE PROJECT SCOPE
============================================================

YieldGrid is a simple D2C (Direct-to-Consumer) farm-to-retail inventory application.

Core idea:

FARMER
    ↓
Lists available crops
    ↓
CUSTOMER
    ↓
Browses available crops
    ↓
Adds crops to cart
    ↓
Places order
    ↓
FARMER receives order
    ↓
FARMER updates order status
    ↓
CUSTOMER tracks order
    ↓
Completed order contributes to farmer revenue

There are ONLY TWO USER ROLES:

1. FARMER
2. CUSTOMER

Do not introduce any additional user role unless absolutely required internally.

============================================================
3. REQUIRED FEATURES
============================================================

FARMER MODULE:

- Register
- Login
- Farmer dashboard
- View profile
- Add crop
- View listed crops
- Update crop
- Delete crop
- Manage quantity
- Manage price
- Manage availability
- View incoming customer orders
- Update order status
- View total sales/revenue

CUSTOMER MODULE:

- Register
- Login
- Customer dashboard
- View profile
- Browse available crops
- View crop details
- Add crop to cart
- Remove crop from cart
- Change cart quantity
- View cart total
- Place order
- View order confirmation
- View current order status
- View order history

ORDER STATUS:

Use a Java enum for order status.

Recommended statuses:

PENDING
CONFIRMED
COMPLETED
CANCELLED

Do not create a complicated delivery lifecycle.

============================================================
4. STRICTLY OUT OF SCOPE
============================================================

DO NOT implement any of the following:

- AI / ML
- Gemini
- voice assistant
- computer vision
- crop disease detection
- blockchain
- cryptocurrency
- IoT
- recommendation engine
- dynamic pricing
- logistics optimization
- route planning
- warehouse management
- multi-vendor marketplace
- retailer portal
- supplier portal
- administrator portal
- online payment gateway
- payment processing
- OTP authentication
- email authentication
- cloud deployment
- REST APIs
- Spring Boot
- Hibernate
- JPA
- React
- Angular
- JavaFX
- mobile application
- microservices
- advanced analytics
- real-time notifications
- sophisticated security architecture

Do not add features simply to make the application appear bigger.

============================================================
5. REQUIRED TECHNOLOGY STACK
============================================================

Use only the following primary stack:

Programming Language:
Java

Java Version:
JDK 17 or later

GUI:
Java Swing

Use AWT only where required by Swing for:
- layout managers
- events
- basic GUI support

Database:
MySQL

Database Connectivity:
JDBC

JDBC requirements:
- Connection
- PreparedStatement
- ResultSet
- SQL CRUD operations

IDE compatibility:
- VS Code
- Eclipse
- IntelliJ IDEA
- NetBeans

Do NOT use JavaFX.

Do NOT introduce Spring, Hibernate, Maven, Gradle, or other frameworks unless the existing workspace already requires one.

If a MySQL JDBC driver is required, use MySQL Connector/J as the JDBC driver and keep the dependency setup minimal.

============================================================
6. REQUIRED COURSE-CONCEPT COVERAGE
============================================================

The implementation must visibly demonstrate the Java concepts identified in the CBP presentation.

Do not artificially insert concepts just for the sake of claiming coverage.

Each concept must appear where it makes logical sense.

THE REQUIRED CONCEPTS ARE:

1. Classes
2. Objects
3. Encapsulation
4. Inheritance
5. Interfaces
6. Validation
7. Exception handling
8. Collections
9. ArrayList
10. String handling
11. Object interaction / object relationships
12. Enums
13. Conditional logic
14. Methods
15. Arithmetic operations
16. Java Swing
17. Event handling
18. Layout managers
19. JDBC
20. SQL
21. CRUD operations
22. PreparedStatement
23. Database retrieval

============================================================
7. JAVA CONCEPT MAPPING — MUST BE FOLLOWED
============================================================

Implement and COMMENT the project according to the following academic mapping.

------------------------------------------------------------
MODULE 1: USER REGISTRATION & LOGIN
------------------------------------------------------------

Concepts:
- Classes
- Objects
- Encapsulation
- Validation
- Exception handling

Implementation expectation:

Create a suitable user model.

Prefer an object-oriented hierarchy such as:

abstract class User

with:

Farmer extends User
Customer extends User

Use private fields with getters/setters to demonstrate encapsulation.

Use constructors to initialise objects.

Implement validation for:
- empty fields
- invalid username
- invalid password
- duplicate username
- invalid login credentials

Use exception handling for invalid operations.

IMPORTANT:
Every relevant section must contain meaningful comments such as:

// OOP CONCEPT: Encapsulation
// The fields are private so that the object's state cannot be modified
// directly from outside the class. Access is provided through methods.

// OOP CONCEPT: Inheritance
// Farmer inherits common user properties and behaviour from User.
// This avoids duplicating shared authentication/profile fields.

// OOP CONCEPT: Exception Handling
// Invalid input is handled using exception handling instead of
// allowing the GUI/application to terminate unexpectedly.

Do NOT write useless comments such as:
"this is a variable"
"this method adds user"

Comments should explain the Java concept being demonstrated.

------------------------------------------------------------
MODULE 2: FARMER DASHBOARD
------------------------------------------------------------

Concepts:
- Classes
- Inheritance
- Interfaces
- Event handling

Create a FarmerDashboard Swing screen.

A Farmer should be able to:

- view profile
- manage crops
- view orders
- update order status
- view revenue

Create a small meaningful interface if appropriate.

For example:

interface DashboardActions

or a domain-relevant interface.

The interface must have an actual purpose.

Example concept comment:

// OOP CONCEPT: Interface
// This interface defines operations that a dashboard-related class
// must support without forcing the implementation into one class.

// OOP CONCEPT: Event Handling
// This button action is connected to the corresponding business
// operation using Swing's event-driven programming model.

Do not create interfaces only to tick a syllabus box.

------------------------------------------------------------
MODULE 3: CROP INVENTORY
------------------------------------------------------------

Concepts:
- Collections
- ArrayList
- CRUD
- Exception handling

Create Crop as a proper model class.

Suggested fields:

cropId
farmerId
cropName
description
quantity
price
available

Use an ArrayList where it makes academic sense, especially for:
- displaying current cart items
- temporarily holding retrieved records
- managing collections before/after database operations

The database remains the persistent source of truth.

Farmer must be able to:

CREATE crop
READ crop
UPDATE crop
DELETE crop

Validate:
- crop name
- quantity > 0
- price > 0

Handle invalid operations with exceptions.

Meaningful comments are REQUIRED:

// JAVA COLLECTIONS: ArrayList
// ArrayList is used to maintain an ordered collection of Crop
// objects retrieved for display or temporary application-level processing.

// CRUD CONCEPT: CREATE
// This operation inserts a new crop record into the database.

// CRUD CONCEPT: UPDATE
// This operation modifies an existing crop record selected by the farmer.

// EXCEPTION HANDLING
// Invalid quantity and price values are rejected before the database
// operation is attempted.

------------------------------------------------------------
MODULE 4: CUSTOMER BROWSE & CART
------------------------------------------------------------

Concepts:
- Collections
- String handling
- Object interaction
- Event handling

Customer should see available crops.

Implement:
- crop listing
- crop detail view
- add to cart
- remove from cart
- update quantity
- calculate cart total

Use:

ArrayList<CartItem>

or equivalent.

CartItem should contain a reference to Crop or relevant crop information.

This should demonstrate OBJECT INTERACTION.

Example:

Customer
interacts with
Cart

Cart
contains
CartItem

CartItem
references
Crop

Add comments:

// OBJECT INTERACTION:
// A CartItem interacts with the Crop object to obtain crop identity,
// price and other information required to calculate the cart total.

// COLLECTIONS:
// The cart is represented as an ArrayList because the number of
// selected items changes dynamically during the shopping session.

// STRING HANDLING:
// String operations are used when displaying crop names, descriptions,
// search/filter text and order summaries.

// EVENT HANDLING:
// Swing button actions trigger cart operations through ActionListener.

Keep searching/filtering simple if included. Do not create an advanced search engine.

------------------------------------------------------------
MODULE 5: ORDER MANAGEMENT
------------------------------------------------------------

Concepts:
- Classes
- Encapsulation
- Collections
- Exception handling

Create:

Order
OrderItem

Use relationships such as:

Order HAS-A collection of OrderItem objects.

Demonstrate composition/object relationships clearly.

When customer places an order:

1. Validate cart is not empty
2. Validate quantities
3. Calculate total
4. Create Order object
5. Create OrderItem objects
6. Store order in MySQL
7. Reduce crop inventory quantity
8. Clear the cart
9. Show confirmation

Handle:
- empty cart
- insufficient quantity
- invalid crop
- failed database operation

Use transaction handling with JDBC only if necessary to keep inventory and order updates consistent.

Do not build an advanced payment system.

Example comment:

// OOP CONCEPT: Object Relationship
// An Order is composed of multiple OrderItem objects.
// This models the real relationship between one customer order
// and the individual crops purchased within that order.

// EXCEPTION HANDLING:
// The application checks invalid order conditions and reports them
// through the GUI instead of allowing the program to crash.

------------------------------------------------------------
MODULE 6: ORDER TRACKING
------------------------------------------------------------

Concepts:
- Enum
- Conditional logic
- Object relationships

Create:

enum OrderStatus

Example:

PENDING
CONFIRMED
COMPLETED
CANCELLED

Use the enum throughout the application.

Do NOT use arbitrary strings everywhere for order status.

Example:

OrderStatus.PENDING

Comments must explicitly explain why enum is being used:

// JAVA ENUM CONCEPT:
// OrderStatus is represented using an enum because the order can only
// have one value from a predefined set of valid states.
// This is safer and clearer than using unrestricted String values.

// CONDITIONAL LOGIC:
// The dashboard uses conditional logic to decide which actions are
// available based on the current order status.

Customer:
- sees current status
- sees order history

Farmer:
- updates order status

Do not implement real-time tracking or GPS.

------------------------------------------------------------
MODULE 7: REVENUE TRACKING
------------------------------------------------------------

Concepts:
- Methods
- Arithmetic operations
- Collections
- Database retrieval

Create a simple farmer revenue section.

Revenue can be calculated from completed orders.

Example:

totalRevenue = sum of completed order totals

Use methods such as:

calculateOrderTotal()
calculateRevenue()

Demonstrate arithmetic meaningfully.

Use database retrieval to obtain relevant order data.

If appropriate, use an ArrayList<Order> or ArrayList<OrderItem> for retrieved records.

Example comments:

// METHOD CONCEPT:
// Business logic is placed inside a dedicated method so the operation
// can be reused rather than duplicated across different GUI screens.

// ARITHMETIC OPERATIONS:
// Revenue is calculated by summing the totals of completed orders.

// DATABASE RETRIEVAL:
// JDBC retrieves completed orders associated with the logged-in farmer
// before the revenue calculation is performed.

Keep revenue tracking simple:
- total completed orders
- total revenue

Do not build charts or advanced financial analytics.

------------------------------------------------------------
MODULE 8: GUI LAYER
------------------------------------------------------------

Concepts:
- Java Swing
- Event handling
- Layout managers

The entire user interface MUST use Java Swing.

Do NOT use:
- JavaFX
- web pages
- HTML/CSS
- browser UI

Possible Swing screens:

LoginFrame
RegisterFrame
RoleSelectionFrame if actually necessary
FarmerDashboardFrame
CustomerDashboardFrame
CropManagementPanel
CropBrowsePanel
CartPanel
OrderPanel
ProfilePanel
RevenuePanel

Do not create dozens of windows.

Prefer reusable panels where possible.

Use Swing components such as:

JFrame
JPanel
JLabel
JTextField
JPasswordField
JButton
JTable
JTextArea
JComboBox
JSpinner
JOptionPane
JScrollPane

Use suitable layout managers such as:

BorderLayout
GridLayout
GridBagLayout
FlowLayout
BoxLayout

Use ActionListener and other standard Swing event mechanisms.

IMPORTANT COMMENT STYLE:

// JAVA SWING:
// JFrame provides the top-level application window for this module.

// EVENT HANDLING:
// ActionListener is used so that the application responds to the
// user's button click rather than executing the action automatically.

// LAYOUT MANAGER:
// BorderLayout separates the interface into logical regions and
// allows the components to resize with the window.

The comments should make it obvious during viva which Java topic is demonstrated.

============================================================
9. DATABASE LAYER
============================================================

Concepts:
- JDBC
- SQL
- CRUD
- PreparedStatement
- database retrieval

Use MySQL.

Create a straightforward relational schema.

Suggested tables:

users
crops
orders
order_items

Optional only if genuinely required:

cart-related temporary application state should preferably stay in memory.

Suggested users table:
- user_id
- name
- username
- password
- role

Suggested crops table:
- crop_id
- farmer_id
- crop_name
- description
- quantity
- price
- available

Suggested orders table:
- order_id
- customer_id
- farmer_id
- order_date
- total_amount
- status

Suggested order_items table:
- order_item_id
- order_id
- crop_id
- quantity
- unit_price
- subtotal

Do not create unnecessary tables.

The database must support:

INSERT
SELECT
UPDATE
DELETE

Use PreparedStatement for parameterised SQL.

Example comment:

// JDBC CONCEPT:
// JDBC allows the Java application to communicate with MySQL and
// persist information so that data remains available after the
// application is closed.

// PreparedStatement is used to safely supply values to SQL queries
// and keep query construction separate from user input.

// CRUD CONCEPT:
// INSERT creates a new database record.
// SELECT retrieves stored records.
// UPDATE modifies an existing record.
// DELETE removes a selected record.

Create a dedicated DatabaseConnection / DBConnection utility class.

Create DAO classes where useful, for example:

UserDAO
CropDAO
OrderDAO

Do NOT create an enterprise DAO framework.

Keep it understandable to a student.

============================================================
10. PACKAGE / PROJECT STRUCTURE
============================================================

Use a simple package structure.

Suggested:

src/
└── yieldgrid/
    ├── main/
    │   └── Main.java
    │
    ├── model/
    │   ├── User.java
    │   ├── Farmer.java
    │   ├── Customer.java
    │   ├── Crop.java
    │   ├── CartItem.java
    │   ├── Order.java
    │   ├── OrderItem.java
    │   └── OrderStatus.java
    │
    ├── interfaces/
    │   └── DashboardActions.java
    │
    ├── dao/
    │   ├── UserDAO.java
    │   ├── CropDAO.java
    │   └── OrderDAO.java
    │
    ├── exception/
    │   ├── ValidationException.java
    │   ├── InsufficientStockException.java
    │   └── AuthenticationException.java
    │
    ├── service/
    │   ├── AuthenticationService.java
    │   ├── CropService.java
    │   ├── OrderService.java
    │   └── RevenueService.java
    │
    ├── database/
    │   └── DBConnection.java
    │
    └── ui/
        ├── LoginFrame.java
        ├── RegisterFrame.java
        ├── FarmerDashboard.java
        ├── CustomerDashboard.java
        ├── CropManagementPanel.java
        ├── CropBrowsePanel.java
        ├── CartPanel.java
        ├── OrdersPanel.java
        ├── ProfilePanel.java
        └── RevenuePanel.java

The exact structure may be adjusted if there is a simpler and cleaner design.

Do not over-engineer the package structure.

============================================================
11. EXCEPTION DESIGN
============================================================

Create a few meaningful custom exceptions.

For example:

ValidationException
AuthenticationException
InsufficientStockException

Use them only where they make sense.

Demonstrate:

try
catch
throw
throws

where appropriate.

Do not create an unnecessary exception class for every possible error.

Use JOptionPane or appropriate Swing feedback for user-facing errors.

============================================================
12. OBJECT-ORIENTED DESIGN REQUIREMENTS
============================================================

The implementation must clearly demonstrate:

ENCAPSULATION:
- private fields
- getters/setters
- controlled modification of object state

INHERITANCE:
User
→ Farmer
→ Customer

Use inheritance meaningfully.

INTERFACE:
At least one real interface with meaningful responsibility.

POLYMORPHISM:
Where logical, use parent references such as:

User user;

and allow Farmer / Customer objects to be handled through the parent type.

CONSTRUCTORS:
Use constructors for model initialization.

METHODS:
Separate business operations into reusable methods.

OBJECT RELATIONSHIPS:
Order → OrderItem
Cart → CartItem
Customer → Order
Farmer → Crop

Keep these relationships easy to explain in viva.

============================================================
13. COMMENTING REQUIREMENT — VERY IMPORTANT
============================================================

This is one of the most important requirements.

I want the source code to visibly demonstrate that the project was developed to cover the Java course topics.

Add HIGH-QUALITY ACADEMIC COMMENTS.

Do NOT fill every line with pointless comments.

Instead:

A. At the beginning of each important class:

/*
 * JAVA CONCEPT:
 * Encapsulation / Inheritance / ...
 *
 * PURPOSE:
 * Explain why this class exists in YieldGrid.
 */

B. Before important code blocks:

// OOP CONCEPT: Encapsulation
// ...

// INHERITANCE:
// ...

// INTERFACE:
// ...

// EXCEPTION HANDLING:
// ...

// COLLECTIONS:
// ...

// ARRAYLIST:
// ...

// STRING HANDLING:
// ...

// ENUM:
// ...

// CONDITIONAL LOGIC:
// ...

// JAVA SWING:
// ...

// EVENT HANDLING:
// ...

// LAYOUT MANAGER:
// ...

// JDBC:
// ...

// SQL CRUD:
// ...

// PreparedStatement:
// ...

// DATABASE RETRIEVAL:
// ...

// METHOD / REUSABILITY:
// ...

// ARITHMETIC OPERATION:
// ...

// OBJECT RELATIONSHIP:
// ...

C. For every Java concept, there should be at least one clearly
annotated example in the actual source code.

D. Do not falsely annotate ordinary code as a concept.
Only label a section when the concept is actually being demonstrated.

E. Comments must explain WHY the concept is being used,
not merely name the concept.

BAD:
 // ArrayList

GOOD:
 // COLLECTIONS – ArrayList:
 // The cart contains a dynamic number of CartItem objects, so ArrayList
 // is used to maintain the selected items during the shopping session.

============================================================
14. COURSE COVERAGE DOCUMENT
============================================================

Create a file:

COURSE_CONCEPT_MAP.md

This file must contain a table:

| Java Topic | Project Module | Class / Method | How It Is Demonstrated |

Include every required Java concept.

For example:

| Encapsulation | User Model | User.java | Private fields + getters/setters |
| Inheritance | User Management | Farmer.java | Farmer extends User |
| Interface | Farmer Dashboard | DashboardActions.java | Common dashboard contract |
| Exception Handling | Login / Inventory | ValidationException | Invalid data handling |
| Collections | Cart | CartPanel.java | ArrayList<CartItem> |
| Enum | Order Tracking | OrderStatus.java | Fixed order-state values |
| Swing | GUI | FarmerDashboard.java | Swing components |
| Event Handling | GUI | ActionListener | Button-driven operations |
| JDBC | Database | DBConnection.java | Java-to-MySQL connection |
| PreparedStatement | DAO | CropDAO.java | Parameterised SQL |
| CRUD | Crop Inventory | CropDAO.java | Insert/select/update/delete |
| etc. |

The purpose of this file is to make viva preparation easy.

============================================================
15. README REQUIREMENT
============================================================

Create a clear README.md containing:

1. Project title
2. Project objective
3. Features
4. User roles
5. Technology stack
6. Folder structure
7. Database schema
8. Setup instructions
9. How to run
10. Sample login/demo data
11. Java concept mapping
12. Limitations / out-of-scope features

Do not advertise features that do not exist.

============================================================
16. DATABASE SETUP
============================================================

Create:

database/
└── yieldgrid.sql

Include:

- CREATE DATABASE
- USE database
- CREATE TABLE statements
- necessary constraints
- sample seed data

Provide a few realistic sample farmers, customers and crops.

Do not create huge seed datasets.

Sample crops can include:

Tomato
Potato
Onion
Carrot
Spinach
Chilli

Keep sample data simple.

============================================================
17. UI / UX LEVEL
============================================================

The UI should be:

- clean
- readable
- professional enough for a college demonstration
- simple to navigate
- consistent across screens

Use a restrained agricultural visual identity:

- cream/light background
- dark brown
- muted green
- subtle orange/gold accent

This is a CBP, so do not spend excessive code on visual effects.

Prioritise functionality.

Every important operation should have clear user feedback.

============================================================
18. APPLICATION NAVIGATION
============================================================

Recommended navigation:

START
↓
Login / Register
↓
Choose role OR determine role from stored account
↓
Farmer Dashboard OR Customer Dashboard

FARMER:
Dashboard
├── Profile
├── Manage Crops
├── View Orders
├── Update Order Status
└── Revenue

CUSTOMER:
Dashboard
├── Profile
├── Browse Crops
├── Cart
├── Place Order
└── My Orders / Track Order

Keep the navigation straightforward.

============================================================
19. IMPLEMENTATION QUALITY
============================================================

Implement real working logic.

Do NOT generate placeholder buttons like:

"Coming Soon"

Do NOT generate fake database calls.

Do NOT create methods that only print text when actual functionality is required.

Do NOT create screenshots pretending the application works.

Do NOT make UI-only mockups.

The core workflow must actually work:

REGISTER
→ LOGIN
→ FARMER ADDS CROP
→ CUSTOMER SEES CROP
→ CUSTOMER ADDS TO CART
→ CUSTOMER PLACES ORDER
→ FARMER SEES ORDER
→ FARMER UPDATES STATUS
→ CUSTOMER TRACKS STATUS
→ COMPLETED ORDER
→ FARMER REVENUE UPDATES

============================================================
20. DATA CONSISTENCY
============================================================

Important:

When a customer orders a crop:

- verify available quantity
- deduct ordered quantity
- prevent negative stock
- create order
- create order items

When an order is cancelled:

Do not implement a complex refund system.

At minimum, maintain sensible inventory behaviour.

When an order is marked COMPLETED:

include that order in revenue calculation.

Keep the business rules understandable.

============================================================
21. LOGIN / SESSION
============================================================

Implement a simple in-memory current-user session.

For example:

SessionManager

containing the currently logged-in User.

Do not introduce advanced authentication frameworks.

Use the logged-in user ID to restrict:

Farmer:
- only sees/manages own crops
- only sees relevant orders
- only sees own revenue

Customer:
- only sees own orders
- only sees own profile

============================================================
22. DATABASE ACCESS STYLE
============================================================

Keep SQL inside DAO classes.

Example:

UserDAO
CropDAO
OrderDAO

Business logic should not be scattered across every Swing button handler.

Prefer:

GUI
→ Service
→ DAO
→ Database

But keep the architecture simple enough for students to understand.

Do NOT create a huge enterprise architecture.

============================================================
23. BUILD / RUN / TEST
============================================================

After implementing:

1. Compile the project.
2. Fix compile errors.
3. Verify all imports.
4. Verify JDBC driver configuration.
5. Verify MySQL connection.
6. Verify SQL syntax.
7. Test registration.
8. Test login.
9. Test farmer crop CRUD.
10. Test customer browsing.
11. Test cart.
12. Test order placement.
13. Test order tracking.
14. Test revenue.
15. Test invalid inputs.
16. Test insufficient stock.
17. Test cancelling/invalid order cases.
18. Test that one farmer cannot modify another farmer's crops.
19. Test that one customer cannot access another customer's private order data.

Do not stop after generating files.

Actually run the application or compile/test whatever is possible in the current environment.

============================================================
24. DEMO DATA
============================================================

Provide demo usernames in README without storing or displaying passwords.
The application seeds password hashes and local evaluators should create or
reset demo passwords through a controlled local setup process.

============================================================
25. SCOPE DISCIPLINE
============================================================

This instruction is absolute:

BUILD ONLY WHAT IS DEFINED HERE.

Do not add extra modules because you think they would make the project better.

Do not add:
- admin
- seller ratings
- notifications
- payment
- delivery tracking
- chatbot
- AI
- analytics dashboard
- coupons
- recommendations
- reviews
- wishlist
- complex search
- cloud services

unless a minimal internal mechanism is absolutely required for the existing workflow.

The objective is NOT "maximum number of features."

The objective is:

FUNCTIONAL + EXPLAINABLE + OOP-CENTRIC + MEDIUM-SIZED + ACADEMICALLY COMPLETE.

============================================================
26. WHAT "MEDIUM LEVEL" MEANS
============================================================

The project should feel like:

"The project demonstrates a complete Java OOP application."

It should NOT feel like:

"A startup's production software system."

It should also NOT feel like:

"Five Java files demonstrating CRUD."

Target approximately:

- 15–30 Java classes if justified
- multiple packages
- reusable classes
- working Swing UI
- meaningful JDBC integration
- real database operations
- exception classes
- model relationships
- comments showing Java concepts

Do not create classes just to reach a number.

============================================================
27. VIVA / PRESENTATION READINESS
============================================================

Every important implementation decision must be explainable.

For example, students should be able to answer:

Why is User a parent class?
Why use inheritance?
Why use an interface?
Why use private fields?
Why use ArrayList?
Why use enum for order status?
Why use custom exceptions?
Why use Swing?
Why use ActionListener?
Why use layout managers?
Why use JDBC?
Why use PreparedStatement?
What are CRUD operations?
How does an Order relate to OrderItem?
How is revenue calculated?
How is inventory updated after ordering?

Therefore:

Prefer simple, explicit Java code over clever code.

Avoid:
- unnecessary design patterns
- dependency injection
- reflection
- complex generics
- advanced concurrency
- abstraction layers that cannot be explained by students

============================================================
28. FINAL DELIVERABLES
============================================================

At the end, the workspace should contain:

1. Complete Java source code
2. Java Swing GUI
3. MySQL SQL script
4. JDBC database integration
5. README.md
6. COURSE_CONCEPT_MAP.md
7. Any required exception classes
8. Any minimal configuration/setup instructions

The final application must be runnable.

============================================================
29. AGENT EXECUTION INSTRUCTIONS
============================================================

Do NOT merely tell me how to build it.

YOU ARE THE IMPLEMENTATION AGENT.

Perform the implementation in the workspace.

Your workflow should be:

STEP 1
Inspect the existing workspace.

STEP 2
Determine whether a Java project already exists.

STEP 3
Preserve useful existing files only if they belong to YieldGrid.

STEP 4
Create or restructure the project into the clean package structure above.

STEP 5
Implement model classes first.

STEP 6
Implement exceptions and interfaces.

STEP 7
Implement database connection and SQL schema.

STEP 8
Implement DAO classes.

STEP 9
Implement service/business logic.

STEP 10
Implement Swing UI.

STEP 11
Connect Swing actions to service/DAO operations.

STEP 12
Add course-concept comments throughout the important code.

STEP 13
Create COURSE_CONCEPT_MAP.md.

STEP 14
Create README.md.

STEP 15
Compile and test.

STEP 16
Fix all errors.

STEP 17
Run through the complete farmer/customer workflow.

STEP 18
Report exactly:
- files created
- classes created
- concepts covered
- database tables
- how to run
- what was tested
- any remaining setup required

Do not claim success without actually verifying the implementation.

============================================================
30. MOST IMPORTANT RULE
============================================================

Do not optimize for code volume.

Optimize for:

1. Correctness
2. Simplicity
3. Java OOP coverage
4. Swing functionality
5. JDBC/MySQL functionality
6. Explainability
7. CBP-appropriate scope
8. Clean structure
9. Meaningful academic comments
10. Complete end-to-end workflow

Start building the project now.