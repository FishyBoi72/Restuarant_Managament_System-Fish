package org.example.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            String sql = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "passwordHash TEXT NOT NULL," +
                    "role TEXT NOT NULL)";
            stmt.execute(sql);
            System.out.println("Users table created successfully.");

            // Create MenuItems table
            sql = "CREATE TABLE IF NOT EXISTS MenuItems (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "preparationTime INTEGER," +
                    "price REAL NOT NULL," +
                    "ingredients TEXT)";
            stmt.execute(sql);
            System.out.println("MenuItems table created successfully.");

            // Create Orders table
            sql = "CREATE TABLE IF NOT EXISTS Orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "items TEXT NOT NULL," +
                    "totalPrice REAL NOT NULL," +
                    "status TEXT NOT NULL," +
                    "FOREIGN KEY (userId) REFERENCES Users(id))";
            stmt.execute(sql);
            System.out.println("Orders table created successfully.");

            // Create Tables table
            sql = "CREATE TABLE IF NOT EXISTS Tables (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "size INTEGER NOT NULL," +
                    "status TEXT NOT NULL)";
            stmt.execute(sql);
            System.out.println("Tables table created successfully.");

            // Create Inventory table
            sql = "CREATE TABLE IF NOT EXISTS Inventory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ingredientName TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL)";
            stmt.execute(sql);
            System.out.println("Inventory table created successfully.");

            // Create Sales table
            sql = "CREATE TABLE IF NOT EXISTS Sales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "orderId INTEGER NOT NULL," +
                    "revenue REAL NOT NULL," +
                    "date TEXT NOT NULL," +
                    "FOREIGN KEY (orderId) REFERENCES Orders(id))";
            stmt.execute(sql);
            System.out.println("Sales table created successfully.");

            // Seed Tables and Inventory data if not already seeded
            if (!
                    isTableDataSeeded(conn, "Tables")) {
                seedTablesData(conn);
            }
            if (!isTableDataSeeded(conn, "Inventory")) {
                seedInventoryData(conn);
            }

            System.out.println("Database has been initialized.");
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Check if table data has already been seeded
    private static boolean isTableDataSeeded(Connection conn, String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If count > 0, data is already seeded
            }
        } catch (Exception e) {
            System.err.println("Error checking data in " + tableName + ": " + e.getMessage());
        }
        return false;
    }

    // Method to seed the Tables table with fixed data
    private static void seedTablesData(Connection conn) {
        String[] tableTypes = {"Booth", "Round Table", "Outside Table"};
        int[] tableSizes = {4, 6, 4};  // Booth: 4 seats, Round Table: 6 seats, Outside Table: 4 seats
        int[] tableQuantities = {4, 4, 3};  // Booth: 4 tables, Round Table: 4 tables, Outside Table: 3 tables

        try {
            String sql = "INSERT INTO Tables(size, status) VALUES(?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < tableTypes.length; i++) {
                    for (int j = 0; j < tableQuantities[i]; j++) {
                        pstmt.setInt(1, tableSizes[i]);
                        pstmt.setString(2, "Available");  // Default status to 'Available'
                        pstmt.executeUpdate();
                    }
                }
            }
            System.out.println("Tables data has been seeded.");
        } catch (Exception e) {
            System.err.println("Failed to seed tables data: " + e.getMessage());
        }
    }

    // Method to seed the Inventory table with fixed data
    private static void seedInventoryData(Connection conn) {
        String[] iceCreamFlavors = {
                "Vanilla", "Chocolate", "Strawberry", "Mint Chocolate Chip",
                "Cookies and Cream", "Rocky Road", "Butter Pecan", "Coffee",
                "Pistachio", "Mango", "Caramel Swirl", "Coconut",
                "Peanut Butter Cup", "Banana", "Rum Raisin", "Blueberry"
        };

        String[] toppings = {
                "Sprinkles", "Chocolate Chips", "Whipped Cream", "Cherries",
                "Caramel Sauce", "Hot Fudge", "Nuts", "Marshmallows",
                "Oreos", "Gummy Bears"
        };

        String[] sorbetFlavors = {"Lemon", "Raspberry", "Mango", "Pineapple"};
        String[] lemonadeFlavors = {"Classic Lemonade", "Strawberry Lemonade", "Peach Lemonade"};
        String[] sodaFlavors = {"Cola", "Orange Soda", "Root Beer", "Ginger Ale", "Lemon-Lime Soda"};
        String milk = "Milk";  // For milkshakes

        try {
            String sql = "INSERT INTO Inventory(ingredientName, quantity) VALUES(?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Insert ice cream flavors
                for (String flavor : iceCreamFlavors) {
                    pstmt.setString(1, flavor + " Ice Cream");
                    pstmt.setInt(2, 100);  // Set initial quantity
                    pstmt.executeUpdate();
                }

                // Insert toppings
                for (String topping : toppings) {
                    pstmt.setString(1, topping);
                    pstmt.setInt(2, 15);  // Set initial quantity
                    pstmt.executeUpdate();
                }

                // Insert sorbet flavors
                for (String sorbet : sorbetFlavors) {
                    pstmt.setString(1, sorbet + " Sorbet");
                    pstmt.setInt(2, 70);  // Set initial quantity
                    pstmt.executeUpdate();
                }

                // Insert lemonade flavors
                for (String lemonade : lemonadeFlavors) {
                    pstmt.setString(1, lemonade);
                    pstmt.setInt(2, 50);  // Set initial quantity
                    pstmt.executeUpdate();
                }

                // Insert soda flavors
                for (String soda : sodaFlavors) {
                    pstmt.setString(1, soda);
                    pstmt.setInt(2, 50);  // Set initial quantity
                    pstmt.executeUpdate();
                }

                // Insert milk for milkshakes
                pstmt.setString(1, milk);
                pstmt.setInt(2, 200);  // Set initial quantity
                pstmt.executeUpdate();
            }
            System.out.println("Inventory data has been seeded.");
        } catch (Exception e) {
            System.err.println("Failed to seed inventory data: " + e.getMessage());
        }
    }
}






