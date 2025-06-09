package com.bookshelf.main;

// Import class GUI dan manajemen database
// import com.bookshelf.gui.MainFrame;
import com.bookshelf.database.DatabaseManager;
import javax.swing.*;

import com.bookshelf.gui.LoginFrame;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Kelas utama (main class) untuk menjalankan aplikasi Bookshelf.
 * Ini adalah titik masuk utama program (entry point).
 */
public class BookshelfApp {

    /**
     * Method utama yang dijalankan saat aplikasi mulai.
     * Mengatur tampilan (Look and Feel), membuat koneksi ke database,
     * lalu menampilkan halaman login.
     */
    public static void main(String[] args) {
        // Gunakan tampilan sistem (Windows, macOS, Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        try {
            // Dapatkan koneksi database melalui DatabaseManager (singleton)
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Connection conn = dbManager.getConnection();

            // Jalankan GUI di thread Event Dispatch
            SwingUtilities.invokeLater(() -> {
                new LoginFrame(conn).setVisible(true);
            });

        } catch (SQLException ex) {
            // Tampilkan pesan error jika koneksi gagal
            System.err.println("❌ Database connection failed!");
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database:\n\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menampilkan dialog error dan keluar dari aplikasi.
     * @param title   Judul dialog
     * @param message Pesan error
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
     * Menampilkan informasi tentang aplikasi.
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
     * Menampilkan informasi sistem seperti versi Java, OS, dan penggunaan memori.
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
     * Blok static untuk menutup koneksi database saat aplikasi keluar.
     * Ini adalah shutdown hook.
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
