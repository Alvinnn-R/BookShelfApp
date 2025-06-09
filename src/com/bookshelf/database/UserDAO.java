package com.bookshelf.database;

// Import library SQL untuk koneksi dan query
import java.sql.*;

// Import class User dari package model
import com.bookshelf.model.User;

// Kelas UserDAO digunakan untuk mengelola data user di database
public class UserDAO {

    // Properti untuk menyimpan koneksi database
    private Connection conn;

    // Konstruktor: dijalankan saat objek UserDAO dibuat, menerima objek koneksi
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // Method untuk registrasi user baru ke database
    public boolean register(User user) throws SQLException {
        // Query SQL untuk menyimpan username dan password ke tabel users
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword()); // Catatan: sebaiknya password di-hash
        return stmt.executeUpdate() > 0; // Berhasil jika ada baris yang dimasukkan
    }

    // Method untuk login: cek apakah username dan password cocok di database
    public boolean login(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next(); // Login berhasil jika ada data yang cocok
    }
}
