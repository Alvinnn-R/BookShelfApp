package com.bookshelf.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Manager for MySQL Database
 * Handles connection and table creation for Bookshelf application
 */
public class DatabaseManager {
    
    // MySQL Database configuration
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306"; // Ganti ke 3306 jika default MySQL
    private static final String DB_NAME = "bookshelf_db";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
                                        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; // Ganti sesuai password MySQL Anda
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Singleton instance
    private static DatabaseManager instance;
    private Connection connection;
    
    // Private constructor
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Get singleton instance of DatabaseManager
     * @return DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Initialize database - create database and tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName(DB_DRIVER);
            
            // First, connect without database name to create database if needed
            createDatabaseIfNotExists();
            
            // Connect to the specific database
            connect();
            
            // Create tables
            createTables();
            
            System.out.println("MySQL database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            System.err.println("Please make sure mysql-connector-java-x.x.x.jar is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Username and password are correct");
            System.err.println("3. MySQL is accessible on " + DB_HOST + ":" + DB_PORT);
            e.printStackTrace();
        }
    }
    
    /**
     * Create database if it doesn't exist
     * @throws SQLException if database creation fails
     */
    private void createDatabaseIfNotExists() throws SQLException {
        String createDbUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + 
                           "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        try (Connection conn = DriverManager.getConnection(createDbUrl, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + 
                               " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createDbSQL);
            
            System.out.println("Database '" + DB_NAME + "' is ready!");
            
        } catch (SQLException e) {
            System.err.println("Failed to create database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Connect to MySQL database
     * @throws SQLException if connection fails
     */
    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connected to MySQL database: " + DB_NAME);
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }
    
    /**
     * Create necessary tables
     * @throws SQLException if table creation fails
     */
    private void createTables() throws SQLException {
        createBooksTable();
        insertSampleData();
    }
    
    /**
     * Create books table
     * @throws SQLException if table creation fails
     */
    private void createBooksTable() throws SQLException {
        String createBooksTableSQL = """
            CREATE TABLE IF NOT EXISTS books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL,
                isbn VARCHAR(20) UNIQUE,
                genre VARCHAR(100),
                publication_year INT,
                pages INT,
                description TEXT,
                rating DECIMAL(2,1) DEFAULT 0.0,
                status ENUM('Want to Read', 'Reading', 'Read') DEFAULT 'Want to Read',
                date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                date_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_title (title),
                INDEX idx_author (author),
                INDEX idx_genre (genre),
                INDEX idx_status (status),
                INDEX idx_rating (rating)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createBooksTableSQL);
            System.out.println("Books table created successfully!");
        }
    }
    
    /**
     * Insert sample data if table is empty
     * @throws SQLException if insertion fails
     */
    private void insertSampleData() throws SQLException {
        // Check if table is empty
        String countSQL = "SELECT COUNT(*) FROM books";
        try (Statement stmt = connection.createStatement();
             var rs = stmt.executeQuery(countSQL)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample books
                String insertSQL = """
                    INSERT INTO books (title, author, isbn, genre, publication_year, pages, description, rating, status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
                
                try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                    // Sample Book 1
                    pstmt.setString(1, "Clean Code");
                    pstmt.setString(2, "Robert C. Martin");
                    pstmt.setString(3, "978-0132350884");
                    pstmt.setString(4, "Programming");
                    pstmt.setInt(5, 2008);
                    pstmt.setInt(6, 464);
                    pstmt.setString(7, "A handbook of agile software craftsmanship that teaches you how to write better code.");
                    pstmt.setBigDecimal(8, new java.math.BigDecimal("4.5"));
                    pstmt.setString(9, "Read");
                    pstmt.addBatch();
                    
                    // Sample Book 2
                    pstmt.setString(1, "Effective Java");
                    pstmt.setString(2, "Joshua Bloch");
                    pstmt.setString(3, "978-0134685991");
                    pstmt.setString(4, "Programming");
                    pstmt.setInt(5, 2017);
                    pstmt.setInt(6, 416);
                    pstmt.setString(7, "The definitive guide to Java platform best practices from the creator of the Java Collections Framework.");
                    pstmt.setBigDecimal(8, new java.math.BigDecimal("4.8"));
                    pstmt.setString(9, "Reading");
                    pstmt.addBatch();
                    
                    // Sample Book 3
                    pstmt.setString(1, "The Pragmatic Programmer");
                    pstmt.setString(2, "David Thomas & Andrew Hunt");
                    pstmt.setString(3, "978-0135957059");
                    pstmt.setString(4, "Programming");
                    pstmt.setInt(5, 2019);
                    pstmt.setInt(6, 352);
                    pstmt.setString(7, "Your journey to mastery in software development.");
                    pstmt.setBigDecimal(8, new java.math.BigDecimal("0.0"));
                    pstmt.setString(9, "Want to Read");
                    pstmt.addBatch();
                    
                    // Sample Book 4
                    pstmt.setString(1, "Design Patterns");
                    pstmt.setString(2, "Gang of Four");
                    pstmt.setString(3, "978-0201633610");
                    pstmt.setString(4, "Programming");
                    pstmt.setInt(5, 1994);
                    pstmt.setInt(6, 395);
                    pstmt.setString(7, "Elements of reusable object-oriented software design patterns.");
                    pstmt.setBigDecimal(8, new java.math.BigDecimal("4.3"));
                    pstmt.setString(9, "Want to Read");
                    pstmt.addBatch();
                    
                    // Sample Book 5
                    pstmt.setString(1, "Java: The Complete Reference");
                    pstmt.setString(2, "Herbert Schildt");
                    pstmt.setString(3, "978-1260440232");
                    pstmt.setString(4, "Programming");
                    pstmt.setInt(5, 2020);
                    pstmt.setInt(6, 1248);
                    pstmt.setString(7, "Comprehensive guide to Java programming language.");
                    pstmt.setBigDecimal(8, new java.math.BigDecimal("4.2"));
                    pstmt.setString(9, "Reading");
                    pstmt.addBatch();
                    
                    pstmt.executeBatch();
                    System.out.println("Sample data inserted successfully!");
                }
            }
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("MySQL database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get database connection info
     * @return Database connection info
     */
    public String getDatabaseInfo() {
        return String.format("MySQL Database: %s@%s:%s/%s", DB_USERNAME, DB_HOST, DB_PORT, DB_NAME);
    }
    
    /**
     * Test database connection
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                // Test with a simple query
                try (Statement stmt = testConn.createStatement();
                     var rs = stmt.executeQuery("SELECT COUNT(*) as book_count FROM books")) {
                    if (rs.next()) {
                        int bookCount = rs.getInt("book_count");
                        System.out.println("MySQL connection test successful! Books count: " + bookCount);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("MySQL connection test failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running (XAMPP/WAMP started)");
            System.err.println("2. Database credentials are correct");
            System.err.println("3. MySQL Connector/J is in classpath");
        }
        return false;
    }
    
    /**
     * Execute SQL script (for maintenance or updates)
     * @param sql SQL script to execute
     * @return true if successful
     */
    public boolean executeSQLScript(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("SQL script execution failed: " + sql);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get database statistics
     * @return Database statistics as string
     */
    public String getDatabaseStats() {
        StringBuilder stats = new StringBuilder();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total books
            try (var rs = stmt.executeQuery("SELECT COUNT(*) as total FROM books")) {
                if (rs.next()) {
                    stats.append("Total Books: ").append(rs.getInt("total")).append("\n");
                }
            }
            
            // Books by status
            try (var rs = stmt.executeQuery("SELECT status, COUNT(*) as count FROM books GROUP BY status")) {
                stats.append("\nBooks by Status:\n");
                while (rs.next()) {
                    stats.append("- ").append(rs.getString("status")).append(": ")
                         .append(rs.getInt("count")).append("\n");
                }
            }
            
            // Average rating
            try (var rs = stmt.executeQuery("SELECT AVG(rating) as avg_rating FROM books WHERE rating > 0")) {
                if (rs.next()) {
                    stats.append("\nAverage Rating: ").append(String.format("%.1f", rs.getDouble("avg_rating")));
                }
            }
            
        } catch (SQLException e) {
            stats.append("Error retrieving statistics: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
    
    // Main method for testing
    public static void main(String[] args) {
        System.out.println("Testing MySQL DatabaseManager...");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        if (dbManager.testConnection()) {
            System.out.println("✅ MySQL DatabaseManager is working correctly!");
            System.out.println("Connection: " + dbManager.getDatabaseInfo());
            System.out.println("\n" + dbManager.getDatabaseStats());
        } else {
            System.out.println("❌ MySQL DatabaseManager test failed!");
            System.out.println("\nTroubleshooting Steps:");
            System.out.println("1. Start XAMPP/WAMP and ensure MySQL is running");
            System.out.println("2. Check MySQL username/password in code");
            System.out.println("3. Verify MySQL Connector/J JAR is in classpath");
            System.out.println("4. Test phpMyAdmin access: http://localhost/phpmyadmin");
        }
        
        dbManager.closeConnection();
    }
}