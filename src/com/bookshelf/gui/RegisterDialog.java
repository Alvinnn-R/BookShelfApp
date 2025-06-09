package com.bookshelf.gui;

// Import komponen GUI
import javax.swing.*;
import java.awt.*;

// Import event handling & database
import java.awt.event.*;
import com.bookshelf.database.UserDAO;
import com.bookshelf.model.User;
import java.sql.*;

/**
 * Kelas RegisterDialog adalah jendela pop-up (modal) untuk registrasi user baru.
 * Kelas ini menggunakan inheritance dari JDialog.
 */
public class RegisterDialog extends JDialog {
    private JTextField usernameField;         // Input untuk username
    private JPasswordField passwordField;     // Input password (disensor)

    /**
     * Konstruktor: Membuat dialog pendaftaran
     * @param parent  Frame induk (LoginFrame)
     * @param userDAO Objek akses database user
     */
    public RegisterDialog(JFrame parent, UserDAO userDAO) {
        super(parent, "Register", true); // Judul dialog dan modal = true (blokir parent)
        setSize(300, 150);
        setLayout(new GridLayout(3, 2)); // Tata letak grid: 3 baris, 2 kolom

        // Inisialisasi input
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // Tambahkan label dan field ke dialog
        add(new JLabel("Username"));
        add(usernameField);
        add(new JLabel("Password"));
        add(passwordField);

        // Tombol untuk daftar
        JButton registerBtn = new JButton("Register");
        add(registerBtn);

        // Event saat tombol diklik
        registerBtn.addActionListener(e -> {
            try {
                // Ambil data dari input dan buat objek User
                User newUser = new User(
                    usernameField.getText(),
                    new String(passwordField.getPassword())
                );

                // Panggil method register dari DAO
                if (userDAO.register(newUser)) {
                    JOptionPane.showMessageDialog(this, "Berhasil daftar!");
                    dispose(); // Tutup dialog
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal daftar.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
