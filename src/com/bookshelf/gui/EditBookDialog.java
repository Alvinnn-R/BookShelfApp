package com.bookshelf.gui;

import com.bookshelf.model.Book;
import com.bookshelf.database.BookDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditBookDialog extends JDialog {
    private JTextField tfTitle, tfAuthor, tfIsbn, tfGenre, tfYear, tfPages;
    private JTextArea taDescription;
    private JComboBox<String> cbStatus;
    private JSpinner spRating;
    private boolean succeeded = false;

    public EditBookDialog(JFrame parent, BookDAO bookDAO, BookTableModel tableModel, Book book) {
        super(parent, "Edit Book", true);
        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(parent);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfTitle = new JTextField(book.getTitle(), 25);
        tfAuthor = new JTextField(book.getAuthor(), 25);
        tfIsbn = new JTextField(book.getIsbn(), 25);
        tfGenre = new JTextField(book.getGenre(), 25);
        tfYear = new JTextField(String.valueOf(book.getPublicationYear()), 8);
        tfPages = new JTextField(String.valueOf(book.getPages()), 8);
        taDescription = new JTextArea(book.getDescription(), 3, 25);
        cbStatus = new JComboBox<>(new String[] {
            Book.STATUS_WANT_TO_READ, Book.STATUS_READING, Book.STATUS_READ
        });
        cbStatus.setSelectedItem(book.getStatus());
        spRating = new JSpinner(new SpinnerNumberModel(book.getRating(), 0.0, 5.0, 0.1));

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

        // Button panel
        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener((ActionEvent e) -> {
            if (validateInput()) {
                book.setTitle(tfTitle.getText().trim());
                book.setAuthor(tfAuthor.getText().trim());
                book.setIsbn(tfIsbn.getText().trim());
                book.setGenre(tfGenre.getText().trim());
                try {
                    book.setPublicationYear(Integer.parseInt(tfYear.getText().trim()));
                } catch (NumberFormatException ex) {
                    book.setPublicationYear(0);
                }
                try {
                    book.setPages(Integer.parseInt(tfPages.getText().trim()));
                } catch (NumberFormatException ex) {
                    book.setPages(0);
                }
                book.setDescription(taDescription.getText().trim());
                book.setStatus((String) cbStatus.getSelectedItem());
                book.setRating((Double) spRating.getValue());

                if (bookDAO.updateBook(book)) {
                    JOptionPane.showMessageDialog(this, "Book updated successfully!");
                    succeeded = true;
                    tableModel.setBooks(bookDAO.getAllBooks());
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }

    private void formAdd(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(comp, gbc);
    }

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

    public boolean isSucceeded() {
        return succeeded;
    }
}
