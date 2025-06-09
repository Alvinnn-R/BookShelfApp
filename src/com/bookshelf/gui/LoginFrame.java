package com.bookshelf.gui;

// Import komponen GUI dan event dari Swing dan AWT
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Import class untuk akses database dan model user
import com.bookshelf.database.UserDAO;
import com.bookshelf.model.User;
import java.sql.*;

/**
 * Kelas LoginFrame adalah tampilan login berbasis GUI (Swing).
 * Turunan dari JFrame, artinya menggunakan inheritance dari Swing.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;       
    private JPasswordField passwordField;   
    private UserDAO userDAO;                

    /**
     * Konstruktor: mengatur tampilan dan event login/register
     * @param conn Objek koneksi database yang dikirim dari program utama
     */
    public LoginFrame(Connection conn) {
        // Inisialisasi objek DAO
        userDAO = new UserDAO(conn);

        // Set properti frame
        setTitle("Login");
        setSize(300, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Tengah layar
        setLayout(new GridLayout(4, 1)); // Tata letak grid

        // Buat field input
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // Buat tombol
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        // Tambahkan komponen ke frame
        add(new JLabel("Username"));
        add(usernameField);
        add(new JLabel("Password"));
        add(passwordField);
        add(loginBtn);
        add(registerBtn);

        // Event tombol login
        loginBtn.addActionListener(e -> loginAction());

        // Event tombol register
        registerBtn.addActionListener(e -> openRegisterDialog());
    }

    /**
     * Method yang dijalankan saat tombol Login ditekan
     * Akan mencoba login ke database dan buka MainFrame jika berhasil
     */
    private void loginAction() {
        try {
            // Cek login ke database
            boolean success = userDAO.login(
                usernameField.getText(),
                new String(passwordField.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                dispose(); // Tutup frame login
                new MainFrame().setVisible(true); // Buka frame utama
            } else {
                JOptionPane.showMessageDialog(this, "Username Belum terdaftar/ Password Salah ");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method untuk membuka jendela registrasi
     */
    private void openRegisterDialog() {
        new RegisterDialog(this, userDAO).setVisible(true);
    }
}
