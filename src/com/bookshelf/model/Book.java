package com.bookshelf.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Book Model Class for Simple Bookshelf Application
 * Represents a book entity with all necessary properties
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
    
    // Status constants
    public static final String STATUS_WANT_TO_READ = "Want to Read";
    public static final String STATUS_READING = "Reading";
    public static final String STATUS_READ = "Read";
    
    // Default Constructor
    public Book() {
        this.dateAdded = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        this.status = STATUS_WANT_TO_READ;
        this.rating = 0.0;
    }
    
    // Constructor for new book
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
    
    // Full Constructor (for database retrieval)
    public Book(int id, String title, String author, String isbn, String genre,
                int publicationYear, int pages, String description, double rating, 
                String status, LocalDateTime dateAdded, LocalDateTime dateUpdated) {
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
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        updateTimestamp();
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
    
    // Utility Methods
    private void updateTimestamp() {
        this.dateUpdated = LocalDateTime.now();
    }
    
    public String getFormattedDateAdded() {
        return dateAdded != null ? dateAdded.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getFormattedDateUpdated() {
        return dateUpdated != null ? dateUpdated.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        // Full stars
        for(int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Half star
        if(hasHalfStar) {
            stars.append("☆");
        }
        
        // Empty stars
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for(int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
    
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "No description available";
        }
        if (description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        return description;
    }
    
    // Validation Methods
    public boolean isValidTitle() {
        return title != null && !title.trim().isEmpty();
    }
    
    public boolean isValidAuthor() {
        return author != null && !author.trim().isEmpty();
    }
    
    public boolean isValidIsbn() {
        if (isbn == null || isbn.trim().isEmpty()) {
            return true; // ISBN is optional
        }
        // Basic ISBN validation (10 or 13 digits with optional hyphens)
        String cleanIsbn = isbn.replaceAll("[^0-9X]", "");
        return cleanIsbn.length() == 10 || cleanIsbn.length() == 13;
    }
    
    public boolean isValidYear() {
        int currentYear = LocalDateTime.now().getYear();
        return publicationYear > 0 && publicationYear <= currentYear;
    }
    
    public boolean isValidPages() {
        return pages > 0;
    }
    
    public boolean isValidRating() {
        return rating >= 0.0 && rating <= 5.0;
    }
    
    public boolean isValidStatus() {
        return status != null && (
            status.equals(STATUS_WANT_TO_READ) ||
            status.equals(STATUS_READING) ||
            status.equals(STATUS_READ)
        );
    }
    
    public boolean isValid() {
        return isValidTitle() && isValidAuthor() && isValidIsbn() && 
               isValidYear() && isValidPages() && isValidRating() && isValidStatus();
    }
    
    // Static helper methods
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
    
    @Override
    public String toString() {
        return String.format("%s by %s (%d) - %s", 
                title, author, publicationYear, status);
    }
    
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