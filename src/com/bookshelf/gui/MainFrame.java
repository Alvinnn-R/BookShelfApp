package com.bookshelf.gui;

import com.bookshelf.database.BookDAO;
import com.bookshelf.model.Book;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;

public class MainFrame extends JFrame {
    private JTable bookTable;
    private BookTableModel tableModel;
    private BookDAO bookDAO;

    public MainFrame() {
        super("Simple Bookshelf Apps");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        bookDAO = new BookDAO();
        tableModel = new BookTableModel();
        bookTable = new JTable(tableModel);

        // Load data from database
        refreshTable();

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Add Book");
        JButton btnEdit = new JButton("Edit Book");
        JButton btnDelete = new JButton("Delete Book");
        JButton btnRefresh = new JButton("Refresh");
        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDelete);
        toolBar.addSeparator();
        toolBar.add(btnRefresh);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);

        // Layout
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> showAddBookDialog());
        btnEdit.addActionListener(e -> showEditBookDialog());
        btnDelete.addActionListener(e -> deleteSelectedBook());
        btnRefresh.addActionListener(e -> refreshTable());
        btnSearch.addActionListener(e -> searchBooks(searchField.getText().trim()));

        // Menu bar
        setJMenuBar(createMenuBar());
    }

    private void refreshTable() {
        List<Book> books = bookDAO.getAllBooks();
        tableModel.setBooks(books);
    }

    private void searchBooks(String keyword) {
        if (keyword.isEmpty()) {
            refreshTable();
        } else {
            List<Book> books = bookDAO.searchBooks(keyword);
            tableModel.setBooks(books);
        }
    }

    private void showAddBookDialog() {
        AddBookDialog dialog = new AddBookDialog(this, bookDAO, tableModel);
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang ingin diedit.");
            return;
        }
        Book book = tableModel.getBookAt(selectedRow);
        EditBookDialog dialog = new EditBookDialog(this, bookDAO, tableModel, book);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang ingin dihapus.");
            return;
        }
        Book book = tableModel.getBookAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus buku \"" + book.getTitle() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(book.getId())) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Buku berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus buku.");
            }
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem miExit = new JMenuItem(new AbstractAction("Exit") {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JMenu menuHelp = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem(new AbstractAction("About") {
            public void actionPerformed(ActionEvent e) {
                com.bookshelf.main.BookshelfApp.showAbout();
            }
        });
        JMenuItem miSystemInfo = new JMenuItem(new AbstractAction("System Info") {
            public void actionPerformed(ActionEvent e) {
                com.bookshelf.main.BookshelfApp.showSystemInfo();
            }
        });

        menuFile.add(miExit);
        menuHelp.add(miAbout);
        menuHelp.add(miSystemInfo);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        return menuBar;
    }
}
