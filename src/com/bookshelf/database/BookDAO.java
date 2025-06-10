package com.bookshelf.database;

import com.bookshelf.model.Book;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Book Data Access Object Implementation for MySQL
 * Handles all database operations for Book entity
 */
public class BookDAO {
    
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
    String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY date_added DESC";  // Filter berdasarkan user_id
    
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
     * CREATE - Add a new book to database
     * @param book Book object to add
     * @return true if successful, false otherwise
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
     * READ - Get book by ID
     * @param id Book ID
     * @return Book object or null if not found
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
     * READ - Get all books
     * @return List of all books
     */
    // Menambahkan method untuk mengambil buku berdasarkan user_id
    public List<Book> getBooksByUserId(int userId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY date_added DESC";
        
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
     * READ - Search books by title, author, or ISBN
     * @param searchTerm Search term
     * @return List of matching books
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
     * READ - Get books by status
     * @param status Book status
     * @return List of books with specified status
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
     * READ - Get books by genre
     * @param genre Book genre
     * @return List of books in specified genre
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
     * READ - Advanced search with multiple filters
     * @param searchTerm Search term (can be null)
     * @param genre Genre filter (can be null)
     * @param status Status filter (can be null)
     * @param minRating Minimum rating (can be null)
     * @return List of books matching criteria
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
     * UPDATE - Update existing book
     * @param book Book with updated information
     * @return true if successful, false otherwise
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
            
            // Set parameters
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
                System.err.println("ISBN already exists: " + book.getIsbn());
            }
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * UPDATE - Update book rating only
     * @param bookId Book ID
     * @param rating New rating
     * @return true if successful, false otherwise
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
     * UPDATE - Update book status only
     * @param bookId Book ID
     * @param status New status
     * @return true if successful, false otherwise
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
     * DELETE - Delete book by ID
     * @param id Book ID
     * @return true if successful, false otherwise
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
     * UTILITY - Get total book count
     * @return Total number of books
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
     * UTILITY - Get book count by status
     * @param status Book status
     * @return Number of books with specified status
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
     * UTILITY - Get all unique genres
     * @return List of unique genres
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
     * UTILITY - Get all unique authors
     * @return List of unique authors
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
     * UTILITY - Check if ISBN exists
     * @param isbn ISBN to check
     * @return true if ISBN exists, false otherwise
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
     * UTILITY - Check if ISBN exists for different book (for updates)
     * @param isbn ISBN to check
     * @param excludeBookId Book ID to exclude
     * @return true if ISBN exists for different book, false otherwise
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
     * UTILITY - Get books with pagination
     * @param offset Starting position (0-based)
     * @param limit Number of books to retrieve
     * @return List of books for the specified page
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
     * UTILITY - Get top rated books
     * @param limit Number of books to retrieve
     * @return List of top rated books
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
     * UTILITY - Get recently added books
     * @param limit Number of books to retrieve
     * @return List of recently added books
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
     * UTILITY - Get reading statistics
     * @return Reading statistics as formatted string
     */
    public String getReadingStatistics() {
        StringBuilder stats = new StringBuilder();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total books and by status
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
                    stats.append("📚 Reading Statistics:\n");
                    stats.append("Total Books: ").append(rs.getInt("total")).append("\n");
                    stats.append("Read: ").append(rs.getInt("read_count")).append("\n");
                    stats.append("Currently Reading: ").append(rs.getInt("reading_count")).append("\n");
                    stats.append("Want to Read: ").append(rs.getInt("want_to_read_count")).append("\n");
                    stats.append("Average Rating: ").append(String.format("%.1f", rs.getDouble("avg_rating"))).append("/5.0\n");
                    stats.append("Total Pages: ").append(String.format("%,d", rs.getInt("total_pages"))).append("\n");
                }
            }
            
            // Top genres
            try (ResultSet rs = stmt.executeQuery("""
                SELECT genre, COUNT(*) as count 
                FROM books 
                WHERE genre IS NOT NULL AND genre != '' 
                GROUP BY genre 
                ORDER BY count DESC 
                LIMIT 5
                """)) {
                
                stats.append("\n📊 Top Genres:\n");
                while (rs.next()) {
                    stats.append("- ").append(rs.getString("genre"))
                         .append(": ").append(rs.getInt("count")).append(" books\n");
                }
            }
            
        } catch (SQLException e) {
            stats.append("Error retrieving statistics: ").append(e.getMessage());
        }
        
        return stats.toString();
    }
    
    /**
     * Helper method to map ResultSet to Book object
     * @param rs ResultSet
     * @return Book object
     * @throws SQLException if mapping fails
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
        
        // Handle timestamps
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