-- ============================================================
-- PROJECT: YieldGrid (Farm-to-Retail Inventory Application)
-- SCRIPT: database/yieldgrid.sql
-- PURPOSE: Relational Database Schema & Seed Data for YieldGrid
-- ============================================================

-- SQL CONCEPT: Database Creation & Selection
CREATE DATABASE IF NOT EXISTS yieldgrid;
USE yieldgrid;

-- ============================================================
-- TABLE: users
-- CONCEPT: Encapsulation & Entity Representation
-- Represents user credentials and profiles for both FARMER and CUSTOMER
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    contact_info VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: crops
-- CONCEPT: Entity Representation & Relational Foreign Key
-- Stores crops listed by farmers with quantity and unit price
-- ============================================================
CREATE TABLE IF NOT EXISTS crops (
    crop_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    crop_name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: orders
-- CONCEPT: Object Relationship & Composition (Order Header)
-- Stores order placement records associated with a Customer and a Farmer
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    farmer_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: order_items
-- CONCEPT: Composition / Order HAS-A collection of OrderItems
-- Relates each individual crop and quantity ordered to an order record
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    crop_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES crops(crop_id) ON DELETE RESTRICT
);

-- ============================================================
-- SEED DATA (Realistic demo data for testing & presentation)
-- ============================================================

-- Insert Sample Farmers and Customers
-- Demo users are seeded with PBKDF2 hashes by the application.
-- Run the application once after creating the schema to insert demo users safely.

-- Insert Sample Crops listed by Farmers
INSERT IGNORE INTO crops (crop_id, farmer_id, crop_name, description, quantity, price, available) VALUES
(1, 1, 'Organic Tomato', 'Freshly harvested farm-fresh red ripe tomatoes (per kg)', 150, 35.00, TRUE),
(2, 1, 'Russet Potato', 'Cleaned grade-A earthy potatoes suitable for all dishes (per kg)', 200, 28.00, TRUE),
(3, 1, 'Red Onion', 'Crisp pungent red onions grown with minimal pesticides (per kg)', 120, 42.00, TRUE),
(4, 2, 'Fresh Carrot', 'Sweet and crunchy orange carrots rich in vitamin A (per kg)', 80, 50.00, TRUE),
(5, 2, 'Organic Spinach', 'Tender green spinach leaves harvested every morning (per bunch)', 90, 20.00, TRUE),
(6, 2, 'Guntur Green Chilli', 'Spicy aromatic green chillies freshly plucked (per kg)', 60, 65.00, TRUE);
