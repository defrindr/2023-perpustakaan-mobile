package com.example.perpusmini.models;

import com.example.perpusmini.enums.StatusPinjam;

public class PinjamModel {
    private  String uid, tglPinjam, tglKembali, buku_reference, user_reference;
    private int denda, rating;
    private StatusPinjam status;
    private Book buku;
    private User user;

    public PinjamModel() {}

    public PinjamModel(String uid, String tglPinjam, String tglKembali, String buku_reference, String user_reference, int denda, StatusPinjam status, Book buku, User user) {
        this.uid = uid;
        this.tglPinjam = tglPinjam;
        this.tglKembali = tglKembali;
        this.buku_reference = buku_reference;
        this.user_reference = user_reference;
        this.denda = denda;
        this.status = status;
        this.buku = buku;
        this.user = user;
        this.rating = 0;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Book getBuku() {
        return buku;
    }

    public void setBuku(Book buku) {
        this.buku = buku;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(String tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    public String getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(String tglKembali) {
        this.tglKembali = tglKembali;
    }

    public String getBuku_reference() {
        return buku_reference;
    }

    public void setBuku_reference(String buku_reference) {
        this.buku_reference = buku_reference;
    }

    public String getUser_reference() {
        return user_reference;
    }

    public void setUser_reference(String user_reference) {
        this.user_reference = user_reference;
    }

    public int getDenda() {
        return denda;
    }

    public void setDenda(int denda) {
        this.denda = denda;
    }

    public StatusPinjam getStatus() {
        return status;
    }

    public void setStatus(StatusPinjam status) {
        this.status = status;
    }
}
