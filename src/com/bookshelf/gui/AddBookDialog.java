package com.bookshelf.gui;

import com.bookshelf.database.BookDAO;
import com.bookshelf.model.Book;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

// Kelas AddBookDialog adalah dialog (jendela pop-up) untuk menambah buku baru
public class AddBookDialog extends JDialog {
    // Komponen input untuk data buku
    private JTextField tfTitle, tfAuthor, tfIsbn, tfGenre, tfYear, tfPages;
    private JTextArea taDescription;
    private JComboBox<String> cbStatus;
    private JSpinner spRating;
    // Penanda apakah penambahan buku berhasil
    private boolean succeeded = false;
    private int userId;


    public AddBookDialog(JFrame parent, BookDAO bookDAO, BookTableModel tableModel, int userId) {
        super(parent, "Add New Book", true);
        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(parent);
        this.userId = userId;
        // Panel form input
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inisialisasi field input
        tfTitle = new JTextField(25);
        tfAuthor = new JTextField(25);
        tfIsbn = new JTextField(25);
        tfGenre = new JTextField(25);
        tfYear = new JTextField(8);
        tfPages = new JTextField(8);
        taDescription = new JTextArea(3, 25);
        cbStatus = new JComboBox<>(new String[] {
            Book.STATUS_WANT_TO_READ, Book.STATUS_READING, Book.STATUS_READ
        });
        spRating = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 5.0, 0.1)); // Rating 0-5

        // Menambahkan komponen ke form secara berurutan
        int row = 0;
        formAdd(form, gbc, row++, "Title", tfTitle);
        formAdd(form, gbc, row++, "Author", tfAuthor);
        formAdd(form, gbc, row++, "ISBN", tfIsbn);
        formAdd(form, gbc, row++, "Genre", tfGenre);
        formAdd(form, gbc, row++, "Year", tfYear);
        formAdd(form, gbc, row++, "Pages", tfPages);
        formAdd(form, gbc, row++, "Description", new JScrollPane(taDescription));
        formAdd(form, gbc, row++, "Status", cbStatus);
        formAdd(form, gbc, row++, "Rating", spRating);

        add(form, BorderLayout.CENTER);

        // Panel tombol Save dan Cancel
        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        // Aksi saat tombol Save diklik
        btnSave.addActionListener((ActionEvent e) -> {
            if (validateInput()) { // Validasi input
                Book book = new Book();
                book.setTitle(tfTitle.getText().trim());
                book.setAuthor(tfAuthor.getText().trim());
                book.setIsbn(tfIsbn.getText().trim());
                book.setGenre(tfGenre.getText().trim());
                try {
                    book.setPublicationYear(Integer.parseInt(tfYear.getText().trim()));
                } catch (NumberFormatException ex) {
                    book.setPublicationYear(0); // Jika input tidak valid, set 0
                }
                try {
                    book.setPages(Integer.parseInt(tfPages.getText().trim()));
                } catch (NumberFormatException ex) {
                    book.setPages(0); // Jika input tidak valid, set 0
                }
                book.setDescription(taDescription.getText().trim());
                book.setStatus((String) cbStatus.getSelectedItem());
                book.setRating((Double) spRating.getValue());
        
                if (bookDAO.addBook(book)) {
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                    succeeded = true;
        
                    // Memperbarui data buku berdasarkan userId setelah menambah buku
                    tableModel.setBooks(bookDAO.getBooksByUserId(userId));
                    tableModel.fireTableDataChanged(); // Menyegarkan tampilan JTable
        
                    dispose();  // Menutup dialog setelah menambah buku
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnCancel.addActionListener(e -> dispose());
    }

    // Fungsi untuk menambah label dan komponen ke form dengan GridBagLayout
    private void formAdd(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    // Validasi input wajib (judul dan penulis tidak boleh kosong)
    private boolean validateInput() {
        if (tfTitle.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.");
            return false;
        }
        if (tfAuthor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Author is required.");
            return false;
        }
        // Bisa tambahkan validasi lain sesuai kebutuhan
        return true;
    }

    // Mengecek apakah proses tambah buku berhasil
    public boolean isSucceeded() {
        return succeeded;
    }
}
