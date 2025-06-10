package com.bookshelf.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Manager untuk mengelola koneksi dan operasi database MySQL
 */
public class DatabaseManager {
    
    // ===== Konfigurasi koneksi database MySQL =====
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306"; // Ganti ke 3306 jika default MySQL
    private static final String DB_NAME = "bookshelf_db";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
                                        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // ===== Singleton instance =====
    private static DatabaseManager instance;
    private Connection connection;
    
    // ===== Konstruktor private agar hanya bisa diakses dari dalam class (Singleton) =====
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Mendapatkan instance tunggal DatabaseManager (pola Singleton)
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Inisialisasi database: load driver, buat database jika belum ada, koneksi, dan buat tabel
     */
    private void initializeDatabase() {
        try {
            // Memuat driver JDBC MySQL
            Class.forName(DB_DRIVER);
            
            // Koneksi tanpa nama database untuk membuat database jika belum ada
            createDatabaseIfNotExists();
            
            // Koneksi ke database yang sudah ada
            connect();
            
            // Membuat tabel-tabel yang diperlukan
            createTables();
            
            System.out.println("MySQL database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver tidak ditemukan!");
            System.err.println("Pastikan mysql-connector-java-x.x.x.jar sudah ada di classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Inisialisasi database gagal!");
            System.err.println("Cek:");
            System.err.println("1. MySQL server sudah berjalan");
            System.err.println("2. Username dan password sudah benar");
            System.err.println("3. MySQL dapat diakses di " + DB_HOST + ":" + DB_PORT);
            e.printStackTrace();
        }
    }
    
    /**
     * Membuat database jika belum ada
     */
    private void createDatabaseIfNotExists() throws SQLException {
        String createDbUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + 
                           "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        try (Connection conn = DriverManager.getConnection(createDbUrl, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Membuat database jika belum ada
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + 
                               " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createDbSQL);
            
            System.out.println("Database '" + DB_NAME + "' siap digunakan!");
            
        } catch (SQLException e) {
            System.err.println("Gagal membuat database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Membuka koneksi ke database MySQL
     */
    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Berhasil terhubung ke database MySQL: " + DB_NAME);
        }
    }
    
    /**
     * Mendapatkan objek koneksi database
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }
    
    /**
     * Membuat tabel-tabel yang diperlukan di database
     */
    private void createTables() throws SQLException {
        createBooksTable();
        insertSampleData();
    }
    
    /**
     * Membuat tabel 'books' jika belum ada
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
            System.out.println("Tabel books berhasil dibuat!");
        }
    }
    
    /**
     * Menambahkan data contoh (sample) jika tabel masih kosong
     */
    private void insertSampleData() throws SQLException {
        // Mengecek apakah tabel kosong
        String countSQL = "SELECT COUNT(*) FROM books";
        try (Statement stmt = connection.createStatement();
             var rs = stmt.executeQuery(countSQL)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Jika kosong, masukkan beberapa buku contoh
                String insertSQL = """
                    INSERT INTO books (title, author, isbn, genre, publication_year, pages, description, rating, status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
                
                try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                    // Buku contoh 1
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
                    
                    // Buku contoh 2
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
                    
                    // Buku contoh 3
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
                    
                    // Buku contoh 4
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
                    
                    // Buku contoh 5
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
                    System.out.println("Data contoh berhasil dimasukkan!");
                }
            }
        }
    }
    
    /**
     * Menutup koneksi database
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database MySQL ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi database!");
            e.printStackTrace();
        }
    }
    
    /**
     * Mendapatkan info koneksi database
     */
    public String getDatabaseInfo() {
        return String.format("MySQL Database: %s@%s:%s/%s", DB_USERNAME, DB_HOST, DB_PORT, DB_NAME);
    }
    
    /**
     * Menguji koneksi database
     */
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                // Tes dengan query sederhana
                try (Statement stmt = testConn.createStatement();
                     var rs = stmt.executeQuery("SELECT COUNT(*) as book_count FROM books")) {
                    if (rs.next()) {
                        int bookCount = rs.getInt("book_count");
                        System.out.println("Tes koneksi MySQL berhasil! Jumlah buku: " + bookCount);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Tes koneksi MySQL gagal!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Cek:");
            System.err.println("1. MySQL server sudah berjalan (XAMPP/WAMP sudah start)");
            System.err.println("2. Username/password database sudah benar");
            System.err.println("3. MySQL Connector/J sudah ada di classpath");
        }
        return false;
    }
    
    /**
     * Menjalankan perintah SQL (untuk maintenance atau update)
     */
    public boolean executeSQLScript(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Eksekusi SQL gagal: " + sql);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mendapatkan statistik database (jumlah buku, status, rata-rata rating)
     */
    public String getDatabaseStats() {
        StringBuilder stats = new StringBuilder();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total buku
            try (var rs = stmt.executeQuery("SELECT COUNT(*) as total FROM books")) {
                if (rs.next()) {
                    stats.append("Total Buku: ").append(rs.getInt("total")).append("\n");
                }
            }
            
            // Buku berdasarkan status
            try (var rs = stmt.executeQuery("SELECT status, COUNT(*) as count FROM books GROUP BY status")) {
                stats.append("\nBuku berdasarkan Status:\n");
                while (rs.next()) {
                    stats.append("- ").append(rs.getString("status")).append(": ")
                         .append(rs.getInt("count")).append("\n");
                }
            }
            
            // Rata-rata rating
            try (var rs = stmt.executeQuery("SELECT AVG(rating) as avg_rating FROM books WHERE rating > 0")) {
                if (rs.next()) {
                    stats.append("\nRata-rata Rating: ").append(String.format("%.1f", rs.getDouble("avg_rating")));
                }
            }
            
        } catch (SQLException e) {
            stats.append("Gagal mengambil statistik: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
    
    // ===== Main method untuk testing mandiri =====
    public static void main(String[] args) {
        System.out.println("Testing MySQL DatabaseManager...");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        if (dbManager.testConnection()) {
            System.out.println("✅ MySQL DatabaseManager berjalan dengan baik!");
            System.out.println("Koneksi: " + dbManager.getDatabaseInfo());
            System.out.println("\n" + dbManager.getDatabaseStats());
        } else {
            System.out.println("❌ Tes DatabaseManager gagal!");
            System.out.println("\nLangkah Troubleshooting:");
            System.out.println("1. Jalankan XAMPP/WAMP dan pastikan MySQL aktif");
            System.out.println("2. Cek username/password MySQL di kode");
            System.out.println("3. Pastikan MySQL Connector/J JAR sudah ada di classpath");
            System.out.println("4. Tes akses phpMyAdmin: http://localhost/phpmyadmin");
        }
        
        dbManager.closeConnection();
    }
}