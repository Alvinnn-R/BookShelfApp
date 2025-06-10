package com.bookshelf.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kelas model Book untuk Simple Bookshelf Application
 * Merepresentasikan entitas buku beserta semua properti yang diperlukan
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int publicationYear;
    private int pages;
    private String description;
    private double rating;
    private String status; // "Want to Read", "Reading", "Read"
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
    private int userId;
    
    // ===== Konstanta status =====
    public static final String STATUS_WANT_TO_READ = "Want to Read";
    public static final String STATUS_READING = "Reading";
    public static final String STATUS_READ = "Read";
    
    // ===== Konstruktor default (untuk buku baru) =====
    public Book() {
        this.dateAdded = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        this.status = STATUS_WANT_TO_READ;
        this.rating = 0.0;
    }
    
    // ===== Konstruktor untuk buku baru dengan data utama =====
    public Book(String title, String author, String isbn, String genre, 
                int publicationYear, int pages, String description) {
        this();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.pages = pages;
        this.description = description;
    }
    
    // ===== Konstruktor lengkap (biasanya untuk data dari database) =====
    public Book(int id, String title, String author, String isbn, String genre,
                int publicationYear, int pages, String description, double rating, 
                String status, LocalDateTime dateAdded, LocalDateTime dateUpdated, int userId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.pages = pages;
        this.description = description;
        this.rating = rating;
        this.status = status;
        this.dateAdded = dateAdded;
        this.dateUpdated = dateUpdated;
        this.userId = userId;
    }
    
    // ===== Getter dan Setter untuk setiap properti =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        updateTimestamp(); // update waktu jika data diubah
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { 
        this.author = author; 
        updateTimestamp();
    }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { 
        this.isbn = isbn; 
        updateTimestamp();
    }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { 
        this.genre = genre; 
        updateTimestamp();
    }
    
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { 
        this.publicationYear = publicationYear; 
        updateTimestamp();
    }
    
    public int getPages() { return pages; }
    public void setPages(int pages) { 
        this.pages = pages; 
        updateTimestamp();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        updateTimestamp();
    }
    
    public double getRating() { return rating; }
    // Setter rating dengan validasi (0.0 - 5.0)
    public void setRating(double rating) { 
        if (rating >= 0.0 && rating <= 5.0) {
            this.rating = rating; 
            updateTimestamp();
        }
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        updateTimestamp();
    }
    
    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
    
    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDateTime dateUpdated) { this.dateUpdated = dateUpdated; }
    
    // ===== Utility Methods =====
    // Update waktu terakhir diubah
    private void updateTimestamp() {
        this.dateUpdated = LocalDateTime.now();
    }
    
    // Format tanggal ditambahkan
    public String getFormattedDateAdded() {
        return dateAdded != null ? dateAdded.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    // Format tanggal diupdate
    public String getFormattedDateUpdated() {
        return dateUpdated != null ? dateUpdated.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    // Menghasilkan string rating dalam bentuk bintang (★)
    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        // Bintang penuh
        for(int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Setengah bintang
        if(hasHalfStar) {
            stars.append("☆");
        }
        
        // Bintang kosong
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for(int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
    
    // Mengambil deskripsi singkat (maks 100 karakter)
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "No description available";
        }
        if (description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        return description;
    }
    
    // ===== Metode Validasi =====
    // Validasi judul tidak kosong
    public boolean isValidTitle() {
        return title != null && !title.trim().isEmpty();
    }
    // Validasi penulis tidak kosong
    public boolean isValidAuthor() {
        return author != null && !author.trim().isEmpty();
    }
    // Validasi ISBN (boleh kosong, jika ada harus 10/13 digit)
    public boolean isValidIsbn() {
        if (isbn == null || isbn.trim().isEmpty()) {
            return true; // ISBN opsional
        }
        // Validasi sederhana: 10 atau 13 digit (boleh ada tanda hubung)
        String cleanIsbn = isbn.replaceAll("[^0-9X]", "");
        return cleanIsbn.length() == 10 || cleanIsbn.length() == 13;
    }
    // Validasi tahun terbit (tidak boleh di masa depan)
    public boolean isValidYear() {
        int currentYear = LocalDateTime.now().getYear();
        return publicationYear > 0 && publicationYear <= currentYear;
    }
    // Validasi jumlah halaman (> 0)
    public boolean isValidPages() {
        return pages > 0;
    }
    // Validasi rating (0.0 - 5.0)
    public boolean isValidRating() {
        return rating >= 0.0 && rating <= 5.0;
    }
    // Validasi status
    public boolean isValidStatus() {
        return status != null && (
            status.equals(STATUS_WANT_TO_READ) ||
            status.equals(STATUS_READING) ||
            status.equals(STATUS_READ)
        );
    }
    // Validasi keseluruhan data buku
    public boolean isValid() {
        return isValidTitle() && isValidAuthor() && isValidIsbn() && 
               isValidYear() && isValidPages() && isValidRating() && isValidStatus();
    }
    
    // ===== Static helper untuk pilihan status dan genre =====
    public static String[] getStatusOptions() {
        return new String[]{STATUS_WANT_TO_READ, STATUS_READING, STATUS_READ};
    }
    
    public static String[] getGenreOptions() {
        return new String[]{
            "Fiction", "Non-Fiction", "Mystery", "Romance", "Science Fiction",
            "Fantasy", "Biography", "History", "Programming", "Business",
            "Self-Help", "Health", "Travel", "Cooking", "Art", "Other"
        };
    }
    
    // ===== Override toString untuk tampilan ringkas buku =====
    @Override
    public String toString() {
        return String.format("%s by %s (%d) - %s", 
                title, author, publicationYear, status);
    }
    
    // ===== Override equals dan hashCode untuk membandingkan buku =====
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return id == book.id || (isbn != null && isbn.equals(book.isbn));
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, isbn);
    }
}