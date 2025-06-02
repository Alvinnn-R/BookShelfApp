package com.bookshelf.gui;

import com.bookshelf.model.Book;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books = new ArrayList<>();
    private final String[] columns = {"ID", "Title", "Author", "ISBN", "Genre", "Year", "Pages", "Status", "Rating"};

    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }

    public Book getBookAt(int row) {
        return books.get(row);
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getId();
            case 1: return book.getTitle();
            case 2: return book.getAuthor();
            case 3: return book.getIsbn();
            case 4: return book.getGenre();
            case 5: return book.getPublicationYear();
            case 6: return book.getPages();
            case 7: return book.getStatus();
            case 8: return book.getRating();
            default: return null;
        }
    }
}
