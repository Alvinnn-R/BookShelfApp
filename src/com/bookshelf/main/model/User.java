package com.bookshelf.model;

/**
 * Kelas User berfungsi sebagai model data untuk pengguna.
 * Menyimpan data dasar berupa username dan password.
 */
public class User {
    private String username;
    private String password;

    /**
     * Konstruktor untuk membuat objek User baru.
     * @param username Nama pengguna
     * @param password Kata sandi pengguna
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter: Mengambil username
    public String getUsername() {
        return username;
    }

    // Getter: Mengambil password
    public String getPassword() {
        return password;
    }

    // Setter: Mengubah username
    public void setUsername(String username) {
        this.username = username;
    }

    // Setter: Mengubah password
    public void setPassword(String password) {
        this.password = password;
    }
}
