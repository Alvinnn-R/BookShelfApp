package com.bookshelf.main;

import com.bookshelf.database.DatabaseManager;
import javax.swing.*;

/**
 * Kelas utama aplikasi Simple Bookshelf Apps
 * Titik masuk (entry point) aplikasi desktop
 */
public class BookshelfApp {
    
    public static void main(String[] args) {
        // Mengatur tampilan aplikasi mengikuti sistem operasi
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Menyimpan properti nama dan versi aplikasi
        System.setProperty("app.name", "Simple Bookshelf Apps");
        System.setProperty("app.version", "1.0.0");
        
        // Inisialisasi dan tes koneksi database
        System.out.println("=== Simple Bookshelf Apps v1.0.0 ===");
        System.out.println("Initializing application...");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        // Jika koneksi database berhasil, tampilkan GUI utama
        if (dbManager.testConnection()) {
            System.out.println("✅ Database connection successful!");
            
            // Menampilkan window utama aplikasi secara thread-safe
            SwingUtilities.invokeLater(() -> {
                new com.bookshelf.gui.MainFrame().setVisible(true);
            });
            
        } else {
            // Jika koneksi database gagal, tampilkan pesan error
            System.err.println("❌ Database connection failed!");
            showErrorDialog("Database Error", 
                "Could not connect to database.\n\n" +
                "Please check:\n" +
                "1. MySQL JDBC driver (mysql-connector-java-x.x.x.jar) is in classpath\n" +
                "2. MySQL server is running (XAMPP/WAMP)\n" +
                "3. Username/password/port database sudah benar\n" +
                "4. Database bookshelf_db sudah dibuat atau bisa dibuat otomatis"
            );
        }
    }
    
    /**
     * Menampilkan dialog error ke user jika terjadi masalah (misal: koneksi database gagal)
     * @param title Judul dialog
     * @param message Pesan error yang ditampilkan
     */
    private static void showErrorDialog(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null, 
                message, 
                title, 
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        });
    }
    
    /**
     * Menampilkan informasi tentang aplikasi (About)
     */
    public static void showAbout() {
        String aboutText = """
            Simple Bookshelf Apps v1.0.0
            
            A simple desktop application for managing
            your personal book collection.
            
            Features:
            • Add, edit, and delete books
            • Search and filter books
            • Track reading status
            • Rate your books
            • MySQL database storage
            
            Developed with Java Swing
            
            © 2025 Simple Bookshelf Apps
            """;
        
        JOptionPane.showMessageDialog(
            null,
            aboutText,
            "About Simple Bookshelf Apps",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Menampilkan informasi sistem dan aplikasi (Java, OS, memori, database)
     */
    public static void showSystemInfo() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        String systemInfo = String.format("""
            System Information

            Java Version: %s
            Java Vendor: %s
            Operating System: %s %s
            Architecture: %s

            Application Info:
            %s

            Memory Usage:
            Total Memory: %.2f MB
            Free Memory: %.2f MB
            Used Memory: %.2f MB

            %s
            """,
            System.getProperty("java.version"),
            System.getProperty("java.vendor"),
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"),
            dbManager.getDatabaseInfo(),
            Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0),
            Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0),
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0),
            dbManager.getDatabaseStats()
        );
        
        JTextArea textArea = new JTextArea(systemInfo);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            null,
            scrollPane,
            "System Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Shutdown hook untuk membersihkan resource saat aplikasi ditutup
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            try {
                DatabaseManager.getInstance().closeConnection();
                System.out.println("✅ Application shutdown complete!");
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
    }
}