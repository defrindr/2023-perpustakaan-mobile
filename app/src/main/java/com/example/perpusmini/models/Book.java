package com.example.perpusmini.models;

import androidx.annotation.NonNull;

import com.example.perpusmini.enums.KategoriBuku;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Book {

    private String judul, pengarang, gambar, isbn;
    private KategoriBuku kategori;
    private int stok = 0, available = 0, rating = 1;

    public Book() {
    }

    public Book(String isbn, String judul, String pengarang, int rating, int stok, String gambar, KategoriBuku kategori) {
        this.isbn = isbn;
        this.judul = judul;
        this.pengarang = pengarang;
        this.stok = stok;
        this.rating = rating;
        this.available = stok;
        this.gambar = gambar;
        this.kategori = kategori;
    }

    public KategoriBuku getKategori() {
        return kategori;
    }

    public void setKategori(KategoriBuku kategori) {
        this.kategori = kategori;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

