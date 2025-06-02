package com.bookshelf.main;

// import com.bookshelf.gui.MainFrame;
import com.bookshelf.database.DatabaseManager;
import javax.swing.*;

/**
 * Main Application Class for Simple Bookshelf Apps
 * Entry point for the desktop application
 */
public class BookshelfApp {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Set application properties
        System.setProperty("app.name", "Simple Bookshelf Apps");
        System.setProperty("app.version", "1.0.0");
        
        // Initialize and test database
        System.out.println("=== Simple Bookshelf Apps v1.0.0 ===");
        System.out.println("Initializing application...");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        if (dbManager.testConnection()) {
            System.out.println("✅ Database connection successful!");
            
            // Tampilkan GUI utama
            SwingUtilities.invokeLater(() -> {
                new com.bookshelf.gui.MainFrame().setVisible(true);
            });
            
        } else {
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
     * Show error dialog to user
     * @param title Dialog title
     * @param message Error message
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
     * Show application info
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
     * Show system information
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
     * Shutdown hook for cleanup
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