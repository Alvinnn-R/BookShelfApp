package com.bookshelf.gui;

import com.bookshelf.model.Book;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// Ini adalah contoh inheritance di Java.
public class BookTableModel extends AbstractTableModel {
    // List untuk menyimpan data buku yang akan ditampilkan di tabel
    private List<Book> books = new ArrayList<>();
    // Nama-nama kolom pada tabel
    private final String[] columns = {"ID", "Title", "Author", "ISBN", "Genre", "Year", "Pages", "Status", "Rating"};

    // Method untuk mengatur ulang data buku pada tabel
    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged(); // Memberitahu tabel bahwa data telah berubah, agar tampilan diperbarui
    }

    // Mengambil objek Book pada baris tertentu
    public Book getBookAt(int row) {
        return books.get(row);
    }

    // Mengembalikan jumlah baris (jumlah buku)
    @Override
    public int getRowCount() {
        return books.size();
    }

    // Mengembalikan jumlah kolom (jumlah atribut buku yang ditampilkan)
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    // Mengembalikan nama kolom berdasarkan indeks
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    // Mengembalikan data yang akan ditampilkan pada sel tabel (baris dan kolom tertentu)
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getId();               // ID buku
            case 1: return book.getTitle();            // Judul buku
            case 2: return book.getAuthor();           // Penulis
            case 3: return book.getIsbn();             // ISBN
            case 4: return book.getGenre();            // Genre
            case 5: return book.getPublicationYear();  // Tahun terbit
            case 6: return book.getPages();            // Jumlah halaman
            case 7: return book.getStatus();           // Status (misal: Read, Reading)
            case 8: return book.getRating();           // Rating buku
            default: return null;
        }
    }
}
