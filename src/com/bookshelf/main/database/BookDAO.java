package com.bookshelf.database;

import com.bookshelf.model.Book;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Book Data Access Object Implementation for MySQL
 * Kelas ini menangani semua operasi database untuk entitas Book
 */
public class BookDAO {
    
    // Objek untuk mengelola koneksi database
    private DatabaseManager dbManager;
    private int userId;  // Menyimpan userId untuk digunakan dalam query
    

  // Konstruktor BookDAO untuk menerima userId
  public BookDAO(int userId) {
    this.dbManager = DatabaseManager.getInstance();
    this.userId = userId;  // Menyimpan userId untuk digunakan dalam query
}

// Method untuk mendapatkan buku berdasarkan user_id
public List<Book> getBooksByUserId() {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY date_added ASC";  // Filter berdasarkan user_id
    
    try (Connection conn = dbManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, userId);  // Menggunakan user_id untuk memfilter buku
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            books.add(new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getString("genre"),
                rs.getInt("publication_year"),
                rs.getInt("pages"),
                rs.getString("description"),
                rs.getDouble("rating"),
                rs.getString("status"),
                rs.getTimestamp("date_added").toLocalDateTime(),
                rs.getTimestamp("date_updated").toLocalDateTime(),
                rs.getInt("user_id")
            ));            
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return books;
}
    
    
    /**
     * CREATE - Menambah buku baru ke database
     * @param book Objek Book yang akan ditambah
     * @return true jika berhasil, false jika gagal
     */
    public boolean addBook(Book book) {

        String query = "INSERT INTO books (title, author, isbn, genre, publication_year, pages, description, rating, status, date_added, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getGenre());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setInt(6, book.getPages());
            stmt.setString(7, book.getDescription());
            stmt.setDouble(8, book.getRating());
            stmt.setString(9, book.getStatus());
            stmt.setTimestamp(10, Timestamp.valueOf(book.getDateAdded()));
            stmt.setInt(11, this.userId);  // Pastikan user_id di-set dengan benar
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
        
    /**
     * READ - Mengambil buku berdasarkan ID
     * @param id ID buku
     * @return Objek Book atau null jika tidak ditemukan
     */
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * READ - Mengambil semua buku
     * @return List berisi semua buku
     */
    // Menambahkan method untuk mengambil buku berdasarkan user_id
    public List<Book> getBooksByUserId(int userId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY date_added ASC";
        
        try (Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);  // Menggunakan user_id untuk memfilter buku
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs)); // Menambahkan buku ke list
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by user_id: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    
    /**
     * READ - Cari buku berdasarkan judul, penulis, atau ISBN
     * @param searchTerm Kata kunci pencarian
     * @return List buku yang cocok
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT * FROM books 
            WHERE title LIKE ? 
               OR author LIKE ?
               OR isbn LIKE ?
            ORDER BY title
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * READ - Mengambil buku berdasarkan status
     * @param status Status buku
     * @return List buku dengan status yang ditentukan
     */
    public List<Book> getBooksByStatus(String status) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE status = ? ORDER BY title";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * READ - Mengambil buku berdasarkan genre
     * @param genre Genre buku
     * @return List buku dalam genre yang ditentukan
     */
    public List<Book> getBooksByGenre(String genre) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE genre = ? ORDER BY title";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, genre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books by genre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * READ - Pencarian lanjutan dengan beberapa filter
     * @param searchTerm Kata kunci pencarian (boleh null)
     * @param genre Filter genre (boleh null)
     * @param status Filter status (boleh null)
     * @param minRating Rating minimum (boleh null)
     * @return List buku yang cocok dengan kriteria
     */
    public List<Book> searchBooksWithFilters(String searchTerm, String genre, String status, Double minRating) {
        List<Book> books = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (genre != null && !genre.trim().isEmpty()) {
            sqlBuilder.append(" AND genre = ?");
            params.add(genre);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            sqlBuilder.append(" AND status = ?");
            params.add(status);
        }
        
        if (minRating != null) {
            sqlBuilder.append(" AND rating >= ?");
            params.add(minRating);
        }
        
        sqlBuilder.append(" ORDER BY title");
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books with filters: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * UPDATE - Memperbarui informasi buku yang sudah ada
     * @param book Buku dengan informasi terbaru
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateBook(Book book) {
        String sql = """
            UPDATE books SET 
                title = ?, author = ?, isbn = ?, genre = ?, 
                publication_year = ?, pages = ?, description = ?, 
                rating = ?, status = ?
            WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameter sesuai urutan kolom di database
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getPublicationYear());
            pstmt.setInt(6, book.getPages());
            pstmt.setString(7, book.getDescription());
            pstmt.setBigDecimal(8, new java.math.BigDecimal(book.getRating()));
            pstmt.setString(9, book.getStatus());
            pstmt.setInt(10, book.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Book updated successfully: " + book.getTitle());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("ISBN sudah ada: " + book.getIsbn());
            }
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UPDATE - Memperbarui rating buku saja
     * @param bookId ID buku
     * @param rating Rating baru
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateBookRating(int bookId, double rating) {
        String sql = "UPDATE books SET rating = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, new java.math.BigDecimal(rating));
            pstmt.setInt(2, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating book rating: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UPDATE - Memperbarui status buku saja
     * @param bookId ID buku
     * @param status Status baru
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateBookStatus(int bookId, String status) {
        String sql = "UPDATE books SET status = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating book status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * DELETE - Menghapus buku berdasarkan ID
     * @param id ID buku
     * @return true jika berhasil, false jika gagal
     */
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Book deleted successfully (ID: " + id + ")");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UTILITY - Mengambil total jumlah buku
     * @return Total jumlah buku
     */
    public int getTotalBooksCount() {
        String sql = "SELECT COUNT(*) as total FROM books";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total books count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * UTILITY - Mengambil jumlah buku berdasarkan status
     * @param status Status buku
     * @return Jumlah buku dengan status yang ditentukan
     */
    public int getBooksCountByStatus(String status) {
        String sql = "SELECT COUNT(*) as count FROM books WHERE status = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books count by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * UTILITY - Mengambil semua genre yang unik
     * @return List genre yang unik
     */
    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre FROM books WHERE genre IS NOT NULL AND genre != '' ORDER BY genre";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String genre = rs.getString("genre");
                if (genre != null && !genre.trim().isEmpty()) {
                    genres.add(genre);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all genres: " + e.getMessage());
            e.printStackTrace();
        }
        
        return genres;
    }
    
    /**
     * UTILITY - Mengambil semua penulis yang unik
     * @return List penulis yang unik
     */
    public List<String> getAllAuthors() {
        List<String> authors = new ArrayList<>();
        String sql = "SELECT DISTINCT author FROM books WHERE author IS NOT NULL ORDER BY author";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String author = rs.getString("author");
                if (author != null && !author.trim().isEmpty()) {
                    authors.add(author);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all authors: " + e.getMessage());
            e.printStackTrace();
        }
        
        return authors;
    }
    
    /**
     * UTILITY - Memeriksa apakah ISBN sudah ada
     * @param isbn ISBN yang akan diperiksa
     * @return true jika ISBN sudah ada, false jika tidak
     */
    public boolean isIsbnExists(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) as count FROM books WHERE isbn = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking ISBN existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UTILITY - Memeriksa apakah ISBN sudah ada untuk buku yang berbeda (untuk pembaruan)
     * @param isbn ISBN yang akan diperiksa
     * @param excludeBookId ID buku yang akan dikecualikan dari pemeriksaan
     * @return true jika ISBN ada untuk buku yang berbeda, false jika tidak
     */
    public boolean isIsbnExistsForDifferentBook(String isbn, int excludeBookId) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) as count FROM books WHERE isbn = ? AND id != ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            pstmt.setInt(2, excludeBookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking ISBN for different book: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UTILITY - Mengambil buku dengan paginasi
     * @param offset Posisi awal (0-based)
     * @param limit Jumlah buku yang akan diambil
     * @return List buku untuk halaman yang ditentukan
     */
    public List<Book> getBooksWithPagination(int offset, int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY date_added DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books with pagination: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * UTILITY - Mengambil buku dengan rating tertinggi
     * @param limit Jumlah buku yang akan diambil
     * @return List buku dengan rating tertinggi
     */
    public List<Book> getTopRatedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE rating > 0 ORDER BY rating DESC, title ASC LIMIT ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting top rated books: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * UTILITY - Mengambil buku yang baru ditambahkan
     * @param limit Jumlah buku yang akan diambil
     * @return List buku yang baru ditambahkan
     */
    public List<Book> getRecentlyAddedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY date_added DESC LIMIT ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recently added books: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * UTILITY - Mengambil statistik membaca
     * @return Statistik membaca dalam bentuk string terformat
     */
    public String getReadingStatistics() {
        StringBuilder stats = new StringBuilder();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total buku dan berdasarkan status
            try (ResultSet rs = stmt.executeQuery("""
                SELECT 
                    COUNT(*) as total,
                    SUM(CASE WHEN status = 'Read' THEN 1 ELSE 0 END) as read_count,
                    SUM(CASE WHEN status = 'Reading' THEN 1 ELSE 0 END) as reading_count,
                    SUM(CASE WHEN status = 'Want to Read' THEN 1 ELSE 0 END) as want_to_read_count,
                    AVG(CASE WHEN rating > 0 THEN rating ELSE NULL END) as avg_rating,
                    SUM(pages) as total_pages
                FROM books
                """)) {
                
                if (rs.next()) {
                    stats.append("📚 Statistik Membaca:\n");
                    stats.append("Total Buku: ").append(rs.getInt("total")).append("\n");
                    stats.append("Sudah Dibaca: ").append(rs.getInt("read_count")).append("\n");
                    stats.append("Sedang Dibaca: ").append(rs.getInt("reading_count")).append("\n");
                    stats.append("Ingin Dibaca: ").append(rs.getInt("want_to_read_count")).append("\n");
                    stats.append("Rating Rata-rata: ").append(String.format("%.1f", rs.getDouble("avg_rating"))).append("/5.0\n");
                    stats.append("Total Halaman: ").append(String.format("%,d", rs.getInt("total_pages"))).append("\n");
                }
            }
            
            // Genre teratas
            try (ResultSet rs = stmt.executeQuery("""
                SELECT genre, COUNT(*) as count 
                FROM books 
                WHERE genre IS NOT NULL AND genre != '' 
                GROUP BY genre 
                ORDER BY count DESC 
                LIMIT 5
                """)) {
                
                stats.append("\n📊 Genre Teratas:\n");
                while (rs.next()) {
                    stats.append("- ").append(rs.getString("genre"))
                         .append(": ").append(rs.getInt("count")).append(" buku\n");
                }
            }
            
        } catch (SQLException e) {
            stats.append("Error retrieving statistics: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
    
    /**
     * Helper method untuk mengubah ResultSet menjadi objek Book
     * @param rs ResultSet dari query
     * @return Objek Book
     * @throws SQLException jika mapping gagal
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setGenre(rs.getString("genre"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setPages(rs.getInt("pages"));
        book.setDescription(rs.getString("description"));
        book.setRating(rs.getBigDecimal("rating").doubleValue());
        book.setStatus(rs.getString("status"));
        
        // Handle waktu penambahan dan update
        Timestamp dateAdded = rs.getTimestamp("date_added");
        Timestamp dateUpdated = rs.getTimestamp("date_updated");
        
        if (dateAdded != null) {
            book.setDateAdded(dateAdded.toLocalDateTime());
        } else {
            book.setDateAdded(LocalDateTime.now());
        }
        
        if (dateUpdated != null) {
            book.setDateUpdated(dateUpdated.toLocalDateTime());
        } else {
            book.setDateUpdated(LocalDateTime.now());
        }
        
        return book;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        System.out.println("Testing MySQL BookDAO...");
        
        int userId = 1; // Gantilah dengan nilai userId yang sebenarnya setelah login

        BookDAO bookDAO = new BookDAO(userId);  // Menggunakan userId yang sudah didefinisikan
        
        // Test get all books
        List<Book> books = bookDAO.getBooksByUserId();
        System.out.println("Total books in database: " + books.size());
    
        if (!books.isEmpty()) {
            System.out.println("First book: " + books.get(0).getTitle() + " by " + books.get(0).getAuthor());
        }
        
        // Test search
        List<Book> searchResults = bookDAO.searchBooks("java");
        System.out.println("Search results for 'java': " + searchResults.size());
        
        // Test counts by status
        System.out.println("Want to Read: " + bookDAO.getBooksCountByStatus("Want to Read"));
        System.out.println("Reading: " + bookDAO.getBooksCountByStatus("Reading"));
        System.out.println("Read: " + bookDAO.getBooksCountByStatus("Read"));
        
        // Test genres
        List<String> genres = bookDAO.getAllGenres();
        System.out.println("Available genres: " + genres);
        
        // Test statistics
        System.out.println("\n" + bookDAO.getReadingStatistics());
        
        System.out.println("✅ MySQL BookDAO test completed!");
    }
}